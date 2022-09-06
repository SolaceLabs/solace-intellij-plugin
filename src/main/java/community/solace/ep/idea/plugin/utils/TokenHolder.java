package community.solace.ep.idea.plugin.utils;

import java.util.Properties;

public class TokenHolder {

    public static Properties props = new Properties();

    static {
        props.put("token", "eyJhbGciOiJSUzI1NiIsImtpZCI6Im1hYXNfcHJvZF8yMDIwMDMyNiIsInR5cCI6IkpXVCJ9.eyJvcmciOiJzb2xhY2VjdG8iLCJvcmdUeXBlIjoiRU5URVJQUklTRSIsInN1YiI6IjY3dHI4dGt1NDEiLCJwZXJtaXNzaW9ucyI6IkFBQUFBSUFQQUFBQWZ6Z0E0QUVBQUFBQUFBQUFBQUFBQUFDQXpvY2hJQWpnTC83L2c1WGZCZDREV1BNRDZ3RT0iLCJhcGlUb2tlbklkIjoiZ3Axd3J3aGcxNm4iLCJpc3MiOiJTb2xhY2UgQ29ycG9yYXRpb24iLCJpYXQiOjE2NjE4ODA2NDh9.BMJmFXK57YdoG05vWsDyWfpgh0Py6L7boTwekI9GOx5HzRbDvrpAUlP-ChHnfbsuXCpR4BPcyFhc0hz5WC2-PyxJJaIMEkxr3RauC_vsmjYTEDEGHRHU6kTfbc7zcn44ka_RQk0hFccPcB5iAi7qqksw2ytyNRolVTnR1tcgGX_LfXAunQ6hW9iQeVbksq82XIK6OSNTiqYUAyFQ8lPOqmlCKmsaPcC_U1whU3HVSmrdDmrZiLk8ZycpQ3ywA3IeffH6gXubKjTV5bLPMvDML0_-B1twh7NawEObyD9Goy8Za6XXf7AVLzBObkDkS9gR9XLQYUnc82ofKK07RG_lNQ");
        props.put("baseUrl", "solace-sso.solace.cloud");
        
        props.put("timeFormat", "relative");
    }
}
