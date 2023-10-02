package com.example.lasya.SocialMediaApp.controller;

import com.example.lasya.SocialMediaApp.bean.UserBean;
import com.example.lasya.SocialMediaApp.service.UserService;
import com.example.lasya.SocialMediaApp.service.UserServiceImpl;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        super();
        this.userService = userService;
    }
    @ApiOperation(value = "This API is used to create a new customer")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 201, message = "Created"), @ApiResponse(code = 400, message = "Bad Request"),
            @ApiResponse(code = 401, message = "Unauthorized"), @ApiResponse(code = 404, message = "Not Found"),
            @ApiResponse(code = 407, message = "Proxy Authentication Required"),
            @ApiResponse(code = 500, message = "Internal Server Error"),
            @ApiResponse(code = 502, message = "Bad Gateway")
    })
    @PostMapping(value = "/api/v1/users")
    public ResponseEntity<UserBean> createUser(@RequestBody UserBean userBean) {
        userBean = userService.createUser(userBean);
        return new ResponseEntity<UserBean>(userBean, HttpStatus.CREATED);
    }

    @ApiOperation(value = "This API is used to retrieve all users")
    @GetMapping(value = "/api/v1/users")
    public ResponseEntity<List<UserBean>> getUsers() {
        return new ResponseEntity<List<UserBean>>(userService.getAllUsers(), HttpStatus.OK);
    }

    @ApiOperation(value = "This API is used to update a user")
    @PutMapping(value = "/api/v1/users/{userId}")
    public ResponseEntity<UserBean> updateUser(@PathVariable int userId,
                                               @RequestBody UserBean userBean) {
        return new ResponseEntity<UserBean>(userService.updateUser(userBean, userId), HttpStatus.OK);
    }

    @ApiOperation(value = "This API is used to retrieve a user by specifying an id")
    @GetMapping(value = "/api/v1/users/{userId}")
    public ResponseEntity<UserBean> getUserById(@PathVariable int userId) {
        UserBean user = userService.getUserById(userId);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @ApiOperation(value = "This API is used to delete a user by specifying an id")
    @DeleteMapping(value = "/api/v1/users/{userId}")
    public boolean deleteUserById(@PathVariable int userId) {
        return userService.deleteUserById(userId);
    }

    @ApiOperation(value = "This API is used to search a user by firstName, lastName, userName or email attributes")
    @GetMapping(value = "/api/v1/users/searchUser/{searchText}")
    public List<UserBean> searchUsers(@PathVariable String searchText){
        logger.info("Received searchText: {}", searchText);
        List<UserBean> result = userService.searchUsers(searchText);
        logger.info("Search result size: {}", result.size());
        return result;
//        return userService.searchUsers(searchText);
    }

    @ApiOperation(value = "This API is used to sign up a user")
    @PostMapping(value = "/api/v1/users/signup")
    public UserBean signUpUser(@RequestBody UserBean userBean){
        return userService.signUpUser(userBean);
    }

    @ApiOperation(value = "This API is used to signing in a user")
    @PostMapping(value = "/api/v1/users/login")
    public UserBean signInUser(@RequestBody Map<String, String> credentials){
        String username = credentials.get("userName");
        String password = credentials.get("password");
        return userService.signInUser(username, password);
    }

    @ApiOperation(value = "This API is used to update password field by providing the user email field")
    @PostMapping(value = "/api/v1/users/updatePassword")
    public UserBean updatePasswordByEmail(@PathVariable String email, @PathVariable String newPassword){
        return userService.updatePasswordByEmail(email, newPassword);
    }

    @ApiOperation(value = "This API is used to update username and bio fields by providing the user id field")
    @PostMapping(value = "/api/v1/users/updateUsernameAndBio")
    public UserBean updateUsernameAndBio(@PathVariable int userId, @PathVariable String newUsername, @PathVariable String newBio){
        return userService.updateUsernameAndBio(userId, newUsername, newBio);
    }

    @ApiOperation(value = "This API is used to update profile picture field by providing the user id field")
    @PostMapping(value = "/api/v1/users/updateProfilePicture")
    public UserBean updateProfilePicture(@PathVariable int userId, @PathVariable byte[] newProfilePicture){
        return userService.updateProfilePicture(userId, newProfilePicture);
    }

    @ApiOperation(value = "This API is used to update firstName and lastname fields by providing the user id field")
    @PostMapping(value = "/api/v1/users/updateFirstNameAndLastName")
    public UserBean updateFirstNameAndLastName(@RequestBody Map<String, Object> params){
        int userId = (int) params.get("userId");
        String newFirstName = (String) params.get("newFirstName");
        String newLastName = (String) params.get("newLastName");
        return userService.updateFirstNameAndLastName(userId, newFirstName, newLastName);
    }

    @ApiOperation(value = "This API is used to update bio field by providing the user id field")
    @PostMapping(value = "/api/v1/users/updateBio")
    public UserBean updateBio(int userId, String newBio){
        return userService.updateBio(userId,newBio);
    }

    @ApiOperation(value = "This API is used to retrieve all users using pagination")
    @GetMapping(value = "/api/v1/users/pagination/")
    public ResponseEntity<List<UserBean>> getUsersWithPagination(
            @RequestParam(name = "page", defaultValue = "0", required = false) int page,
            @RequestParam(name = "size", defaultValue = "5", required = false) int pageSize) {
        return new ResponseEntity<List<UserBean>>(userService.getUsersWithPagination(page, pageSize),
                HttpStatus.OK);
    }

}
