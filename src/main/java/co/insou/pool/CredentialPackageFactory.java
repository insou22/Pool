package co.insou.pool;

public class CredentialPackageFactory {

    private CredentialPackageFactory() {
        throw new UnsupportedOperationException();
    }

    public static PoolCredentialPackage get(String username, String password) {
        return new PoolCredentialPackage(username, password);
    }

}
