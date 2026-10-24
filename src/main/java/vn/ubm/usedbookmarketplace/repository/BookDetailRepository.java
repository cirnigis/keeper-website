
package vn.ubm.usedbookmarketplace.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import vn.ubm.usedbookmarketplace.dto.BookDetail;

@Repository
public class BookDetailRepository {

	private final NamedParameterJdbcTemplate jdbcTemplate;

	public BookDetailRepository(NamedParameterJdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	private static final String DETAIL_SQL = """
			SELECT
			    sl.ListingId AS listingId,
			    b.BookId AS bookId,
			    COALESCE(NULLIF(sl.ListingTitle, ''), b.Title) AS title,
			    b.Description AS description,
			    b.ISBN AS isbn,
			    b.EditionLabel AS editionLabel,
			    b.LanguageCode AS languageCode,
			    COALESCE(
			        (
			            SELECT TOP 1 li.ImageUrl
			            FROM dbo.ListingImages li
			            WHERE li.ListingId = sl.ListingId
			            ORDER BY li.IsPrimary DESC,
			                     li.SortOrder ASC,
			                     li.ListingImageId ASC
			        ),
			        b.CoverUrl
			    ) AS coverUrl,
			    (
			        SELECT STRING_AGG(a.AuthorName, ', ')
			        FROM dbo.BookAuthors ba
			        INNER JOIN dbo.Authors a
			            ON a.AuthorId = ba.AuthorId
			        WHERE ba.BookId = b.BookId
			          AND a.IsActive = 1
			    ) AS authorName,
			    c.CategoryName AS categoryName,
			    p.PublisherName AS publisherName,
			    sl.ConditionCode AS conditionCode,
			    sl.UnitPrice AS unitPrice,
			    sl.StockQuantity AS stockQuantity,
			    s.ShopName AS shopName,
			    s.ShopSlug AS shopSlug,
			    s.Description AS shopDescription
			FROM dbo.ShopListings sl
			INNER JOIN dbo.Books b
			    ON b.BookId = sl.BookId
			INNER JOIN dbo.Categories c
			    ON c.CategoryId = b.CategoryId
			INNER JOIN dbo.Shops s
			    ON s.ShopId = sl.ShopId
			LEFT JOIN dbo.Publishers p
			    ON p.PublisherId = b.PublisherId
			WHERE sl.ListingId = :listingId
			  AND sl.ListingStatus = 'ACTIVE'
			  AND sl.StockQuantity > 0
			  AND b.IsActive = 1
			  AND c.IsActive = 1
			  AND s.IsActive = 1
			  AND s.ApprovalStatus = 'APPROVED'
			""";

	private final RowMapper<BookDetail> rowMapper = (rs, rowNum) -> {
		BookDetail book = new BookDetail();

		book.setListingId(rs.getLong("listingId"));
		book.setBookId(rs.getLong("bookId"));
		book.setTitle(rs.getString("title"));
		book.setDescription(rs.getString("description"));
		book.setIsbn(rs.getString("isbn"));
		book.setEditionLabel(rs.getString("editionLabel"));
		book.setLanguageCode(rs.getString("languageCode"));
		book.setCoverUrl(rs.getString("coverUrl"));
		book.setAuthorName(rs.getString("authorName"));
		book.setCategoryName(rs.getString("categoryName"));
		book.setPublisherName(rs.getString("publisherName"));
		book.setConditionCode(rs.getString("conditionCode"));
		book.setUnitPrice(rs.getBigDecimal("unitPrice"));
		book.setStockQuantity(rs.getInt("stockQuantity"));
		book.setShopName(rs.getString("shopName"));
		book.setShopSlug(rs.getString("shopSlug"));
		book.setShopDescription(rs.getString("shopDescription"));

		return book;
	};

	public Optional<BookDetail> findActiveListingById(Long listingId) {
		MapSqlParameterSource params = new MapSqlParameterSource().addValue("listingId", listingId);

		List<BookDetail> results = jdbcTemplate.query(DETAIL_SQL, params, rowMapper);

		return results.stream().findFirst();
	}
}
