package a2.common.security;

/**
 * Text-based permission for the authenticated user. This information will
 * decide which application(s) he/she can access.
 *
 * @since 1.0.0
 */
public class Permission {

    public static final Permission INVENTORY = new Permission("inventory");
    public static final Permission ORDER = new Permission("order");
    public static final Permission SHIPPING = new Permission("shipping");

    private final String content;

    public Permission(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof Permission)) return false;

        Permission that = (Permission) object;

        return !(getContent() != null ? !getContent().equals(that.getContent()) : that.getContent() != null);

    }

    @Override
    public int hashCode() {
        return getContent() != null ? getContent().hashCode() : 0;
    }
}
