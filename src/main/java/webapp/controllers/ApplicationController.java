package webapp.controllers;

// Import framework depenencies  - required to handle the HTTP request and send a response
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestParam;

import org.springframework.beans.factory.annotation.Autowired;
import java.util.*;

import webapp.models.*;
import webapp.dao.ProductDao;
import webapp.dao.CategoryDao;


// The @ annotation identifies this as a Controller class
@Controller
public class ApplicationController {

    // Dao instance for Product
    @Autowired
    private ProductDao productData;

    // Dao Instance for Category
    @Autowired
    private CategoryDao categoryData;

    // index accepts a request parameter, named name,  from the address URL
    // the parameter is optional and has a default value if not provided
    // String name will be assigned the param value
    // Model model is used to pass data to the view
    // This method, index(), serves as the site index - the default page
    // Requests for the root '/' will be handled by this method
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String index(@RequestParam(name = "name", required = false, defaultValue = "") String name, Model model) {

        // add name and its value to the view model object
        model.addAttribute("name", name);

        //Load and return the index view
        return "index";
    }

    // The about page will be accessed using /about from the browser
    @RequestMapping(value = "/about", method = RequestMethod.GET)
    public String about() {

        //Load and return the about view
        return "about";
    }

    // This method displays the product page
    @RequestMapping(value = "/products", method = RequestMethod.GET)
    // Uses a Model instance - which will be passed to a view
    // cat parameter is for category id
    public String products(@RequestParam(name = "cat", required = false, defaultValue = "0") String cat, Model model){

        List<Product> products;

        // Initialise id (default value used to get all products)
        int id = 0;

        // The parameter may be not be valid - which could crash the application
        // This trys to parse the string converting it to an it
        // If successfull id will be assigned the cat value
        // Otherwise - catch any exception
        // If it fails (i.e an exception occurs) id value will not be changed (from 0).
        try { 
            id = Integer.parseInt(cat); 
         }
        catch(NumberFormatException e) {
            System.out.println("Bad input for cat id: " + e);
        }

        // If id is 0 then get all products otherwise get products for cat id
        if (id == 0) {
            // Get the product list from the ProductDao instance
            products = productData.findAll();
        } else {
            products = productData.findByCategory(id);
        }


        // Get the categoru list from the CategoryDao instance
        List<Category> categories = categoryData.findAll();

        model.addAttribute("products", products);
        model.addAttribute("categories", categories);

        // Return the view
        return "products";
    }

        // This method displays the product page
        @RequestMapping(value = "/searchProducts", method = RequestMethod.GET)
        // Uses a Model instance - which will be passed to a view
        // cat parameter is for category id
        public String searchProducts(@RequestParam(name = "search", required = false, defaultValue = "") String search, Model model) {

            // If search is blank then redirect to the products page
            if (search == "") {
                return "redirect:/products";
            }

            // Do the search and get the results
            List<Product> products = productData.findBySearchText(search);
 
            // Get all categories
            List<Category> categories = categoryData.findAll();
    
            model.addAttribute("products", products);
            model.addAttribute("categories", categories);
    
            // Return the view
            return "products";
        }

        // The newProduct page will be accessed using /newProduct from the browser
        @RequestMapping(value = "/newProduct", method = RequestMethod.GET)
        public String newProduct(Model model) {

            // add empty Product to the model 
            model.addAttribute("product", new Product());

            // Get a list of categories and add to the model
            List<Category> categories = categoryData.findAll();
            model.addAttribute("categories", categories);

            // Return the view
            return "newProduct";
        }

        // Handle form submit via HTTP POST
        @RequestMapping(value = "/newProduct", method = RequestMethod.POST)
        // Form data will be supplied as a filled in Product object
        public String createProduct(Product product, RedirectAttributes redirAttrs) {

            // Use the Dao to create the new product
            // To do: check for errors and return to form if any found
            // https://www.journaldev.com/2668/spring-validation-example-mvc-validator
            Product p = productData.create(product);

            if (p != null) {
                redirAttrs.addFlashAttribute("message", "Product added id: " + p.getProductId());
              }
              else {
                redirAttrs.addFlashAttribute("error", "error: product not added");
              }

            // Redirect back to the products list
            // To do: Open a page showing the new product in its own page
            return "redirect:/products";
        }

        // The newProduct page will be accessed using /updateProduct from the browser
        @RequestMapping(value = "/updateProduct", method = RequestMethod.GET)
        public String updateProduct(@RequestParam(name = "id", required = true) String pId, Model model) {

            Product product;
            // Initialise id (default value used to get all products)
            int id = 0;
    
            // The parameter may be not be valid - which could crash the application
            // This trys to parse the string converting it to an it
            // If successfull id will be assigned the cat value
            // Otherwise - catch any exception
            // If it fails (i.e an exception occurs) id value will not be changed (from 0).
            try { 
                id = Integer.parseInt(pId); 
             }
            catch(NumberFormatException e) {
                System.out.println("Bad input for id: " + e);
            }

            // If id is 0 then get all products otherwise get products for cat id
            if (id == 0) {
                // product id=0 does not exist - return to product list
                return "redirect:/products";
            } else {
                // Otherwise find the product matching the id
                product = productData.findById(id);
            }

            // add product to the model 
            model.addAttribute("product", product);

            // Get a list of categories and add to the model
            List<Category> categories = categoryData.findAll();
            model.addAttribute("categories", categories);

            // Return the view
            return "updateProduct";
        }

        // Handle form submit via HTTP POST
        @RequestMapping(value = "/updateProduct", method = RequestMethod.POST)
        // Form data will be supplied as a filled in Product object
        public String editProduct(Product product, RedirectAttributes redirAttrs) {

            // Use the Dao to update the product
            // To do: check for errors and return to form if any found
            // https://www.journaldev.com/2668/spring-validation-example-mvc-validator
            int rows = productData.update(product);

            // output result in server side console
            System.out.println(rows + " rows were updated");

            // Generate a flash message to show the result of this update in the products page
            if (rows >= 1) {
                redirAttrs.addFlashAttribute("message", rows + " rows updated");
              }
              else {
                redirAttrs.addFlashAttribute("error", rows + "error: " + rows + " rows updated");
              }

            // Redirect back to the products list
            return "redirect:/products";
        }


       // Delete Product
       @RequestMapping(value = "/deleteProduct", method = RequestMethod.GET)
       public String deleteProduct(@RequestParam(name = "id", required = true) String pId, RedirectAttributes redirAttrs) {

           // Initialise id (default value used to get all products)
           int id = 0;
   
           // The parameter may be not be valid - which could crash the application
           // This trys to parse the string converting it to an it
           // If successfull id will be assigned the cat value
           // Otherwise - catch any exception
           // If it fails (i.e an exception occurs) id value will not be changed (from 0).
           try { 
               id = Integer.parseInt(pId); 
            }
           catch(NumberFormatException e) {
               System.out.println("Bad input for id: " + e);
           }
           // If id value is greater than 0 then delete - otherwise error
           if (id != 0) {
                int rows = productData.delete(id);
                
                // Verify that something was deleted (rows affected > 1)
                if (rows >= 1) {
                    // Set a flash message confirming the delete
                    redirAttrs.addFlashAttribute("message", rows + " rows deleted");
                }
                else  {
                    // Nothing deleted - set error flash message
                    redirAttrs.addFlashAttribute("error", "Error: Product delete failed");
                }
           }
           else {
               // can't delete id = 0 - show error
                redirAttrs.addFlashAttribute("error", "Nothing to delete");
           }

           // Return to products page
           return "redirect:/products";
       }

}
