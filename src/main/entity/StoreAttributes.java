package entity;

/**
 * This is where we can store our "enum"-like values for certain columns in the database
 * <br>
 * To convert any of these to strings, use [attribute].name()
 */
public class StoreAttributes {
    public enum Role {
        // Number represents where you are in the hierarchy
        USER(0), STAFF(1), MANAGER(2);

        private final int priviledgeLevel;
        private Role(int value) {
            this.priviledgeLevel = value;
        }

        public int getLevel() {
            return priviledgeLevel;
        }
    }

    // CONTROLLER TYPE

    // GAUGE

    // ETC
}
