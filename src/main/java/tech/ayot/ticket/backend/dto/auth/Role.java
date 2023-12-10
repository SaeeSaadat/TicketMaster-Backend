package tech.ayot.ticket.backend.dto.auth;

/**
 * Represents user roles
 */
public enum Role {

    /**
     * User is not logged in
     */
    GUEST("Guest", 0),

    /**
     * User is logged in
     */
    USER("User", 1),

    /**
     * User can view product issues
     */
    STAFF("Staff", 2),

    /**
     * User can manage product
     */
    ADMIN("Admin", 3),

    /**
     * User has access to everything
     */
    SUPER_ADMIN("Super-Admin", 4),
    ;


    /**
     * The role's title
     */
    private final String title;

    /**
     * The role's access level
     */
    private final Integer level;

    Role(String title, Integer level) {
        this.title = title;
        this.level = level;
    }


    public String getTitle() {
        return title;
    }

    public Integer getLevel() {
        return level;
    }
}
