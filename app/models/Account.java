package models;


public class Account {

    private Integer id;
    private Integer studentId;
    private String firstname;
    private String lastname;
    private Address address;
    private String email;
    private String telephone;
    private String password;
    private boolean isAdmin;

    public Account(Integer studentId, String firstname, String lastname, Address address, String email, String telephone, String password, boolean isAdmin) {
        this.studentId = studentId;
        this.firstname = firstname;
        this.lastname = lastname;
        this.address = address;
        this.email = email;
        this.telephone = telephone;
        this.password = password;
        this.isAdmin = isAdmin;
    }


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getStudentId() {
        return studentId;
    }

    public void setStudentId(Integer studentId) {
        this.studentId = studentId;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }
}
