package community.solace.ep.idea.plugin.utils;

import com.intellij.credentialStore.CredentialAttributes;
import com.intellij.credentialStore.CredentialAttributesKt;
import com.intellij.credentialStore.Credentials;
import com.intellij.ide.passwordSafe.PasswordSafe;

public class TokenUtils {

	
	private CredentialAttributes createCredentialAttributes(String key) {
        return new CredentialAttributes(CredentialAttributesKt.generateServiceName("SolaceEventPortal", key));
	}



	public void retreive() {
		String key = "token"; // e.g. serverURL, accountID
		CredentialAttributes credentialAttributes = createCredentialAttributes(key);

//		Credentials credentials = PasswordSafe.getInstance().get(credentialAttributes);
//		if (credentials != null) {
//            String password = credentials.getPasswordAsString();
//			System.out.println(password);
//		}
		
//
//		// or get password only
		String password = PasswordSafe.getInstance().getPassword(credentialAttributes);		
		System.out.println(password);
	}
	
	
	public void store() {
		CredentialAttributes credentialAttributes = createCredentialAttributes("token"); // see previous sample
		Credentials credentials = new Credentials("aaron", "tokentokentoken");
		PasswordSafe.getInstance().set(credentialAttributes, credentials);
	}
	
	
	public static void main(String... args) {


		TokenUtils tu = new TokenUtils();
		tu.store();
		tu.retreive();
		
	}
	
}
