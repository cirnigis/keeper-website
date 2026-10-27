
package vn.ubm.usedbookmarketplace.repository;

import java.util.List;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import vn.ubm.usedbookmarketplace.dto.BookCatalogItem;

@Repository
public class BookCatalogRepository {

	private final NamedParameterJdbcTemplate jdbcTemplate;

	public BookCatalogRepository(NamedParameterJdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	private static final String CATALOG_SQL = """
			WITH ActiveListings AS (
			    SELECT
			        sl.ListingId AS listingId,
			        b.BookId AS bookId,
			        COALESCE(NULLIF(sl.ListingTitle, ''), b.Title) AS title,

			        (
			            SELECT STRING_AGG(a.AuthorName, ', ')
			            FROM dbo.BookAuthors ba
			            INNER JOIN dbo.Authors a
			                ON a.AuthorId = ba.AuthorId
			            WHERE ba.BookId = b.BookId
			              AND a.IsActive = 1
			        ) AS authorName,

			        c.CategoryName AS categoryName,

			        COALESCE(
			            (
			                SELECT TOP 1 li.ImageUrl
			                FROM dbo.ListingImages li
			                WHERE li.ListingId = sl.ListingId
			                ORDER BY
			                    li.IsPrimary DESC,
			                    li.SortOrder ASC,
			                    li.ListingImageId ASC
			            ),
			            b.CoverUrl
			        ) AS coverUrl,

			        sl.UnitPrice AS unitPrice,
			        sl.ConditionCode AS conditionCode,
			        s.ShopName AS shopName,
			        sl.StockQuantity AS stockQuantity,
			        sl.IsFeatured AS isFeatured,
			        sl.CreatedAt AS createdAt,

			        ROW_NUMBER() OVER (
			            PARTITION BY b.BookId
			            ORDER BY
			                sl.UnitPrice ASC,
			                sl.IsFeatured DESC,
			                sl.CreatedAt DESC,
			                sl.ListingId ASC
			        ) AS listingRank

			    FROM dbo.ShopListings sl
			    INNER JOIN dbo.Books b
			        ON b.BookId = sl.BookId
			    INNER JOIN dbo.Categories c
			        ON c.CategoryId = b.CategoryId
			    INNER JOIN dbo.Shops s
			        ON s.ShopId = sl.ShopId

			    WHERE sl.ListingStatus = 'ACTIVE'
			      AND sl.StockQuantity > 0
			      AND b.IsActive = 1
			      AND c.IsActive = 1
			      AND s.IsActive = 1
			      AND s.ApprovalStatus = 'APPROVED'
			)

			SELECT
			    listingId,
			    title,
			    authorName,
			    categoryName,
			    coverUrl,
			    unitPrice,
			    conditionCode,
			    shopName,
			    stockQuantity
			FROM ActiveListings
			WHERE
			    (
			        :keyword IS NOT NULL
			        AND (
			            title LIKE '%' + :keyword + '%'
			            OR EXISTS (
			                SELECT 1
			                FROM dbo.BookAuthors ba
			                INNER JOIN dbo.Authors a
			                    ON a.AuthorId = ba.AuthorId
			                WHERE ba.BookId = ActiveListings.bookId
			                  AND a.IsActive = 1
			                  AND a.AuthorName LIKE '%' + :keyword + '%'
			            )
			        )
			    )
			    OR
			    (
			        :keyword IS NULL
			        AND listingRank = 1
			    )

			ORDER BY
			    isFeatured DESC,
			    createdAt DESC,
			    listingId DESC
			""";

	private final RowMapper<BookCatalogItem> rowMapper = (rs, rowNum) -> {
		BookCatalogItem item = new BookCatalogItem();

		item.setListingId(rs.getLong("listingId"));
		item.setTitle(rs.getString("title"));
		item.setAuthorName(rs.getString("authorName"));
		item.setCategoryName(rs.getString("categoryName"));
		item.setCoverUrl(rs.getString("coverUrl"));
		item.setUnitPrice(rs.getBigDecimal("unitPrice"));
		item.setConditionCode(rs.getString("conditionCode"));
		item.setShopName(rs.getString("shopName"));
		item.setStockQuantity(rs.getInt("stockQuantity"));

		return item;
	};

	public List<BookCatalogItem> findActiveListings(String keyword) {
		String normalizedKeyword = keyword == null || keyword.isBlank() ? null : keyword.trim();

		MapSqlParameterSource params = new MapSqlParameterSource().addValue("keyword", normalizedKeyword);

		return jdbcTemplate.query(CATALOG_SQL, params, rowMapper);
	}
}
