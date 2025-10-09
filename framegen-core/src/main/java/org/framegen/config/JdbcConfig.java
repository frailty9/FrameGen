package org.framegen.config;

import lombok.Data;

import java.util.Objects;
import java.util.Properties;

@Data
public final class JdbcConfig {
    private final String url;
    private final String driverClassName;
    private final String username;
    private final String password;
    private final Properties additionalProps;

    public Properties toProperties() {
        Properties props = new Properties();
        props.setProperty("jdbcUrl", this.url);
        props.setProperty("driverClassName", this.driverClassName);
        props.setProperty("username", this.username);
        props.setProperty("password", this.password);
        props.putAll(this.additionalProps);
        return props;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private String url;
        private String driverClassName;
        private String username;
        private String password;
        private final Properties additionalProps = new Properties();

        public static Builder builder() {
            return new Builder();
        }

        public Builder url(String url) {
            this.url = Objects.requireNonNull(url, "url cannot be null");
            return this;
        }

        public Builder driverClassName(String driverClassName) {
            this.driverClassName = Objects.requireNonNull(driverClassName, "driverClassName cannot be null");
            return this;
        }

        public Builder username(String username) {
            this.username = Objects.requireNonNull(username, "username cannot be null");
            return this;
        }

        public Builder password(String password) {
            this.password = Objects.requireNonNull(password, "password cannot be null");
            return this;
        }

        public Builder config(String key, String value) {
            this.additionalProps.setProperty(key, value);
            return this;
        }

        public Builder connectionTimeout(long milliseconds) {
            return this.config("connectionTimeout", String.valueOf(milliseconds));
        }

        public Builder idleTimeout(long milliseconds) {
            return this.config("idleTimeout", String.valueOf(milliseconds));
        }

        public Builder maximumPoolSize(int size) {
            return this.config("maximumPoolSize", String.valueOf(size));
        }

        public JdbcConfig build() {
            if (this.url == null) {
                throw new IllegalStateException("url is required");
            }
            if (this.driverClassName == null) {
                throw new IllegalStateException("driverClassName is required");
            }
            if (this.username == null) {
                throw new IllegalStateException("username is required");
            }
            if (this.password == null) {
                throw new IllegalStateException("password is required");
            }
            return new JdbcConfig(this.url, this.driverClassName, this.username, this.password, this.additionalProps);
        }
    }
}
