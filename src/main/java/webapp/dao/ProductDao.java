package webapp.dao;

import java.util.List;
import webapp.models.Product;

// ProductDao Interface definition
public interface ProductDao {

	// Return a list containing all the product objects
	public List<Product> findAll();

	// Return a product with matching id
	public Product findById(int id);

	// return a list of products in a category
	public List<Product> findByCategory(int id);

	// return a list of products matching search term
	public List<Product> findBySearchText(String searchText);

	// Create a new product
	public Product create(final Product product);

	// update an exiting product - return the number or fows affected
	public int update(final Product product);

	// Delete product with matching id
	public int delete(int id);	

}