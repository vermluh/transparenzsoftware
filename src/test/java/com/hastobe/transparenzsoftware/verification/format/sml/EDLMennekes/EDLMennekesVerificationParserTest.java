package com.hastobe.transparenzsoftware.verification.format.sml.EDLMennekes;

import com.hastobe.transparenzsoftware.TestUtils;
import com.hastobe.transparenzsoftware.Utils;
import com.hastobe.transparenzsoftware.verification.DecodingException;
import com.hastobe.transparenzsoftware.verification.EncodingType;
import com.hastobe.transparenzsoftware.verification.ValidationException;
import com.hastobe.transparenzsoftware.verification.result.VerificationResult;
import com.hastobe.transparenzsoftware.verification.xml.Meter;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.net.URL;

public class EDLMennekesVerificationParserTest {

    private static final String PUBLIC_KEY = "6DACB9C5466A25B3EB9F6466B53457C84A27448B01A64A278C0A28DAC95F2B45DF39B79918A9A4D2E3551F3FE925D09D";

    @Test
    public void verify_values() throws ValidationException, DecodingException {
        URL url = this.getClass().getResource("/xml/test_mennekes_charging_processes.xml");
        File testfile = new File(url.getFile());
        String testData = TestUtils.readFile(testfile);

        EDLMennekesVerificationParser parser = new EDLMennekesVerificationParser();
        VerificationResult verificationResult = parser.parseAndVerify(testData, EncodingType.hexDecode(PUBLIC_KEY));
        Assert.assertTrue(verificationResult.isVerified());
        Assert.assertEquals(2, verificationResult.getMeters().size());
        Assert.assertEquals(Meter.Type.START, verificationResult.getMeters().get(0).getType());
        Assert.assertEquals(Meter.Type.STOP, verificationResult.getMeters().get(1).getType());
    }


    @Test
    public void verify_publicKey() throws ValidationException, DecodingException {
        URL url = this.getClass().getResource("/xml/test_mennekes_charging_processes.xml");
        File testfile = new File(url.getFile());
        String testData = TestUtils.readFile(testfile);

        EDLMennekesVerificationParser parser = new EDLMennekesVerificationParser();
        Assert.assertEquals(PUBLIC_KEY, parser.parsePublicKey(testData));
    }

    @Test
    public void verify_publicKey_splitted() throws ValidationException, DecodingException {
        URL url = this.getClass().getResource("/xml/test_mennekes_charging_processes.xml");
        File testfile = new File(url.getFile());
        String testData = TestUtils.readFile(testfile);

        EDLMennekesVerificationParser parser = new EDLMennekesVerificationParser();
        Assert.assertEquals(Utils.splitStringToGroups(PUBLIC_KEY, 4), parser.createFormattedKey(testData));
    }
}
