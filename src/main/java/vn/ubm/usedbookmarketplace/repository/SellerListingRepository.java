
package vn.ubm.usedbookmarketplace.repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class SellerListingRepository {

	private final NamedParameterJdbcTemplate jdbc;

	public SellerListingRepository(NamedParameterJdbcTemplate jdbc) {
		this.jdbc = jdbc;
	}

	public List<Map<String, Object>> findListings(String email, Long shopId) {
		String sql = """
				SELECT
				    sl.ListingId AS listingId,
				    sl.ShopId AS shopId,
				    sl.BookId AS bookId,
					sl.ListingTitle AS listingTitle,
					COALESCE(NULLIF(sl.ListingTitle, ''), b.Title) AS title,
				    b.ISBN AS isbn,
				    b.CoverUrl AS coverUrl,
				    sl.ConditionCode AS conditionCode,
				    sl.UnitPrice AS unitPrice,
				    sl.StockQuantity AS stockQuantity,
				    sl.SoldQuantity AS soldQuantity,
				    sl.ListingStatus AS listingStatus,
				    sl.CreatedAt AS createdAt
				FROM dbo.ShopListings sl
				INNER JOIN dbo.Books b ON b.BookId = sl.BookId
				INNER JOIN dbo.Shops s ON s.ShopId = sl.ShopId
				INNER JOIN dbo.Users u ON u.UserId = s.OwnerUserId
				WHERE u.Email = :email
				  AND s.ShopId = :shopId
				ORDER BY sl.CreatedAt DESC
				""";

		return jdbc.queryForList(sql, new MapSqlParameterSource().addValue("email", email).addValue("shopId", shopId));
	}

	public List<Map<String, Object>> searchBooks(String keyword) {
		String sql = """
				SELECT TOP 50
				    b.BookId AS bookId,
				    b.Title AS title,
				    b.ISBN AS isbn,
				    b.PublicationYear AS publicationYear,
				    c.CategoryName AS categoryName,
				    b.CoverUrl AS coverUrl
				FROM dbo.Books b
				INNER JOIN dbo.Categories c ON c.CategoryId = b.CategoryId
				WHERE b.IsActive = 1
				  AND c.IsActive = 1
				  AND (
				      :keyword IS NULL
				      OR b.Title LIKE '%' + :keyword + '%'
				      OR b.ISBN LIKE '%' + :keyword + '%'
				  )
				ORDER BY b.Title
				""";

		return jdbc.queryForList(sql, new MapSqlParameterSource().addValue("keyword",
				keyword == null || keyword.isBlank() ? null : keyword.trim()));
	}

	public boolean isApprovedShopOwner(String email, Long shopId) {
		String sql = """
				SELECT COUNT(1)
				FROM dbo.Shops s
				INNER JOIN dbo.Users u ON u.UserId = s.OwnerUserId
				WHERE u.Email = :email
				  AND s.ShopId = :shopId
				  AND s.ApprovalStatus = 'APPROVED'
				  AND s.IsActive = 1
				""";

		Integer count = jdbc.queryForObject(sql,
				new MapSqlParameterSource().addValue("email", email).addValue("shopId", shopId), Integer.class);

		return count != null && count > 0;
	}

	public void createListing(String email, Long shopId, Long bookId, String listingTitle, String conditionCode,
			BigDecimal unitPrice, int stockQuantity) {

		String sql = """
				INSERT INTO dbo.ShopListings (
				    ShopId,
				    BookId,
				    ListingTitle,
				    ConditionCode,
				    UnitPrice,
				    StockQuantity,
				    SoldQuantity,
				    ListingStatus,
				    IsFeatured,
				    CreatedAt,
				    UpdatedAt
				)
				SELECT
				    s.ShopId,
				    b.BookId,
				    :listingTitle,
				    :conditionCode,
				    :unitPrice,
				    :stockQuantity,
				    0,
				    CASE WHEN :stockQuantity > 0 THEN 'ACTIVE' ELSE 'DRAFT' END,
				    0,
				    SYSUTCDATETIME(),
				    SYSUTCDATETIME()
				FROM dbo.Shops s
				INNER JOIN dbo.Users u ON u.UserId = s.OwnerUserId
				INNER JOIN dbo.Books b ON b.BookId = :bookId AND b.IsActive = 1
				WHERE s.ShopId = :shopId
				  AND u.Email = :email
				  AND s.ApprovalStatus = 'APPROVED'
				  AND s.IsActive = 1
				  AND NOT EXISTS (
				      SELECT 1
				      FROM dbo.ShopListings existing
				      WHERE existing.ShopId = s.ShopId
				        AND existing.BookId = b.BookId
				  )
				""";

		int rows = jdbc.update(sql, new MapSqlParameterSource().addValue("email", email).addValue("shopId", shopId)
				.addValue("bookId", bookId)
				.addValue("listingTitle", listingTitle == null || listingTitle.isBlank() ? null : listingTitle.trim())
				.addValue("conditionCode", conditionCode).addValue("unitPrice", unitPrice)
				.addValue("stockQuantity", stockQuantity));

		if (rows != 1) {
			throw new IllegalArgumentException("Không thể đăng bán. Hãy kiểm tra shop, sách hoặc sản phẩm đã tồn tại.");
		}
	}

	public void updateListing(String email, Long shopId, Long listingId, String listingTitle, String conditionCode,
			BigDecimal unitPrice, int stockQuantity) {

		String sql = """
				UPDATE sl
				SET
				    sl.ListingTitle = :listingTitle,
				    sl.ConditionCode = :conditionCode,
				    sl.UnitPrice = :unitPrice,
				    sl.StockQuantity = :stockQuantity,
				    sl.ListingStatus = CASE
				        WHEN sl.ListingStatus = 'REMOVED' THEN 'REMOVED'
				        WHEN :stockQuantity = 0 THEN 'SOLD_OUT'
				        WHEN sl.ListingStatus = 'SOLD_OUT' THEN 'ACTIVE'
				        ELSE sl.ListingStatus
				    END,
				    sl.UpdatedAt = SYSUTCDATETIME()
				FROM dbo.ShopListings sl
				INNER JOIN dbo.Shops s ON s.ShopId = sl.ShopId
				INNER JOIN dbo.Users u ON u.UserId = s.OwnerUserId
				WHERE sl.ListingId = :listingId
				  AND sl.ShopId = :shopId
				  AND u.Email = :email
				  AND s.ApprovalStatus = 'APPROVED'
				  AND s.IsActive = 1
				  AND sl.ListingStatus <> 'REMOVED'
				""";

		int rows = jdbc.update(sql, new MapSqlParameterSource()
				.addValue("listingTitle", listingTitle == null || listingTitle.isBlank() ? null : listingTitle.trim())
				.addValue("conditionCode", conditionCode).addValue("unitPrice", unitPrice)
				.addValue("stockQuantity", stockQuantity).addValue("listingId", listingId).addValue("shopId", shopId)
				.addValue("email", email));

		if (rows != 1) {
			throw new IllegalArgumentException("Không thể cập nhật sản phẩm hoặc bạn không có quyền thao tác.");
		}
	}

	public void changeListingStatus(String email, Long shopId, Long listingId, String status) {

		String sql = """
				UPDATE sl
				SET
				    sl.ListingStatus = :status,
				    sl.UpdatedAt = SYSUTCDATETIME()
				FROM dbo.ShopListings sl
				INNER JOIN dbo.Shops s ON s.ShopId = sl.ShopId
				INNER JOIN dbo.Users u ON u.UserId = s.OwnerUserId
				WHERE sl.ListingId = :listingId
				  AND sl.ShopId = :shopId
				  AND u.Email = :email
				  AND s.ApprovalStatus = 'APPROVED'
				  AND s.IsActive = 1
				  AND sl.ListingStatus <> 'REMOVED'
				  AND (
				      :status = 'REMOVED'
				      OR (:status = 'HIDDEN' AND sl.StockQuantity > 0)
				      OR (:status = 'ACTIVE' AND sl.StockQuantity > 0)
				  )
				""";

		int rows = jdbc.update(sql, new MapSqlParameterSource().addValue("email", email).addValue("shopId", shopId)
				.addValue("listingId", listingId).addValue("status", status));

		if (rows != 1) {
			throw new IllegalArgumentException(
					"Không thể đổi trạng thái sản phẩm. Hãy kiểm tra tồn kho và quyền sở hữu.");
		}
	}
}
