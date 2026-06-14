package MasterTable.entity.user;

public enum UserRole {

    SENIOR, // can see everything, but not edit/add/delete
    ADMIN, // can do everything
    HOTEL_REPRESENTATIVE // US24: can only see own hotels (master data)

}
