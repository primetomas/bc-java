package org.bouncycastle.cert.test;

import java.io.IOException;
import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Security;
import java.security.Signature;
import java.security.spec.RSAPrivateCrtKeySpec;
import java.security.spec.RSAPublicKeySpec;

import javax.security.auth.x500.X500Principal;

import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.cryptopro.CryptoProObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.Attribute;
import org.bouncycastle.asn1.pkcs.CertificationRequest;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.X500NameBuilder;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.asn1.x509.BasicConstraints;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.Extensions;
import org.bouncycastle.asn1.x509.KeyUsage;
import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.asn1.x9.X9ObjectIdentifiers;
import org.bouncycastle.cert.jcajce.JcaX509ExtensionUtils;
import org.bouncycastle.jce.ECGOST3410NamedCurveTable;
import org.bouncycastle.jce.ECNamedCurveTable;
import org.bouncycastle.jce.interfaces.ECPointEncoder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jce.spec.ECNamedCurveParameterSpec;
import org.bouncycastle.jce.spec.ECParameterSpec;
import org.bouncycastle.jce.spec.ECPrivateKeySpec;
import org.bouncycastle.jce.spec.ECPublicKeySpec;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.ContentVerifierProvider;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.operator.jcajce.JcaContentVerifierProviderBuilder;
import org.bouncycastle.pkcs.PKCS10CertificationRequest;
import org.bouncycastle.pkcs.PKCS10CertificationRequestBuilder;
import org.bouncycastle.pkcs.jcajce.JcaPKCS10CertificationRequest;
import org.bouncycastle.pkcs.jcajce.JcaPKCS10CertificationRequestBuilder;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.encoders.Base64;
import org.bouncycastle.util.encoders.Hex;
import org.bouncycastle.util.test.SimpleTest;

/**
 **/
public class PKCS10Test
    extends SimpleTest
{
    private static final String BC = BouncyCastleProvider.PROVIDER_NAME;

    private byte[] gost3410EC_A = Base64.decode(
  "MIIBOzCB6wIBADB/MQ0wCwYDVQQDEwR0ZXN0MRUwEwYDVQQKEwxEZW1vcyBDbyBMdGQxHjAcBgNV"
 +"BAsTFUNyeXB0b2dyYXBoeSBkaXZpc2lvbjEPMA0GA1UEBxMGTW9zY293MQswCQYDVQQGEwJydTEZ"
 +"MBcGCSqGSIb3DQEJARYKc2RiQGRvbC5ydTBjMBwGBiqFAwICEzASBgcqhQMCAiMBBgcqhQMCAh4B"
 +"A0MABEBYx0P2D7YuuZo5HgdIAUKAXcLBDZ+4LYFgbKjrfStVfH59lc40BQ2FZ7M703hLpXK8GiBQ"
 +"GEYpKaAuQZnMIpByoAAwCAYGKoUDAgIDA0EAgXMcTrhdOY2Er2tHOSAgnMezqrYxocZTWhxmW5Rl"
 +"JY6lbXH5rndCn4swFzXU+YhgAsJv1wQBaoZEWRl5WV4/nA==");

    private byte[] gost3410EC_B = Base64.decode(
  "MIIBPTCB7QIBADCBgDENMAsGA1UEAxMEdGVzdDEWMBQGA1UEChMNRGVtb3MgQ28gTHRkLjEeMBwG"
 +"A1UECxMVQ3J5cHRvZ3JhcGh5IGRpdmlzaW9uMQ8wDQYDVQQHEwZNb3Njb3cxCzAJBgNVBAYTAnJ1"
 +"MRkwFwYJKoZIhvcNAQkBFgpzZGJAZG9sLnJ1MGMwHAYGKoUDAgITMBIGByqFAwICIwIGByqFAwIC"
 +"HgEDQwAEQI5SLoWT7dZVilbV9j5B/fyIDuDs6x4pjqNC2TtFYbpRHrk/Wc5g/mcHvD80tsm5o1C7"
 +"7cizNzkvAVUM4VT4Dz6gADAIBgYqhQMCAgMDQQAoT5TwJ8o+bSrxckymyo3diwG7ZbSytX4sRiKy"
 +"wXPWRS9LlBvPO2NqwpS2HUnxSU8rzfL9fJcybATf7Yt1OEVq");

    private byte[] gost3410EC_C = Base64.decode(
  "MIIBRDCB9AIBADCBhzEVMBMGA1UEAxMMdGVzdCByZXF1ZXN0MRUwEwYDVQQKEwxEZW1vcyBDbyBM"
 +"dGQxHjAcBgNVBAsTFUNyeXB0b2dyYXBoeSBkaXZpc2lvbjEPMA0GA1UEBxMGTW9zY293MQswCQYD"
 +"VQQGEwJydTEZMBcGCSqGSIb3DQEJARYKc2RiQGRvbC5ydTBjMBwGBiqFAwICEzASBgcqhQMCAiMD"
 +"BgcqhQMCAh4BA0MABEBcmGh7OmR4iqqj+ycYo1S1fS7r5PhisSQU2Ezuz8wmmmR2zeTZkdMYCOBa"
 +"UTMNms0msW3wuYDho7nTDNscHTB5oAAwCAYGKoUDAgIDA0EAVoOMbfyo1Un4Ss7WQrUjHJoiaYW8"
 +"Ime5LeGGU2iW3ieAv6es/FdMrwTKkqn5dhd3aL/itFg5oQbhyfXw5yw/QQ==");
    
    private byte[] gost3410EC_ExA = Base64.decode(
     "MIIBOzCB6wIBADB/MQ0wCwYDVQQDEwR0ZXN0MRUwEwYDVQQKEwxEZW1vcyBDbyBMdGQxHjAcBgNV"
   + "BAsTFUNyeXB0b2dyYXBoeSBkaXZpc2lvbjEPMA0GA1UEBxMGTW9zY293MQswCQYDVQQGEwJydTEZ"
   + "MBcGCSqGSIb3DQEJARYKc2RiQGRvbC5ydTBjMBwGBiqFAwICEzASBgcqhQMCAiQABgcqhQMCAh4B"
   + "A0MABEDkqNT/3f8NHj6EUiWnK4JbVZBh31bEpkwq9z3jf0u8ZndG56Vt+K1ZB6EpFxLT7hSIos0w"
   + "weZ2YuTZ4w43OgodoAAwCAYGKoUDAgIDA0EASk/IUXWxoi6NtcUGVF23VRV1L3undB4sRZLp4Vho"
   + "gQ7m3CMbZFfJ2cPu6QyarseXGYHmazoirH5lGjEo535c1g==");

    private byte[] gost3410EC_ExB = Base64.decode(
      "MIIBPTCB7QIBADCBgDENMAsGA1UEAxMEdGVzdDEWMBQGA1UEChMNRGVtb3MgQ28gTHRkLjEeMBwG"
    + "A1UECxMVQ3J5cHRvZ3JhcGh5IGRpdmlzaW9uMQ8wDQYDVQQHEwZNb3Njb3cxCzAJBgNVBAYTAnJ1"
    + "MRkwFwYJKoZIhvcNAQkBFgpzZGJAZG9sLnJ1MGMwHAYGKoUDAgITMBIGByqFAwICJAEGByqFAwIC"
    + "HgEDQwAEQMBWYUKPy/1Kxad9ChAmgoSWSYOQxRnXo7KEGLU5RNSXA4qMUvArWzvhav+EYUfTbWLh"
    + "09nELDyHt2XQcvgQHnSgADAIBgYqhQMCAgMDQQAdaNhgH/ElHp64mbMaEo1tPCg9Q22McxpH8rCz"
    + "E0QBpF4H5mSSQVGI5OAXHToetnNuh7gHHSynyCupYDEHTbkZ");

    public String getName()
    {
        return "PKCS10CertRequest";
    }

    private void generationTest(int keySize, String keyName, String sigName, String provider)
        throws Exception
    {
        KeyPairGenerator kpg = KeyPairGenerator.getInstance(keyName, "BC");

        kpg.initialize(keySize);

        KeyPair kp = kpg.genKeyPair();


        X500NameBuilder x500NameBld = new X500NameBuilder(BCStyle.INSTANCE);

        x500NameBld.addRDN(BCStyle.C, "AU");
        x500NameBld.addRDN(BCStyle.O, "The Legion of the Bouncy Castle");
        x500NameBld.addRDN(BCStyle.L, "Melbourne");
        x500NameBld.addRDN(BCStyle.ST, "Victoria");
        x500NameBld.addRDN(BCStyle.EmailAddress, "feedback-crypto@bouncycastle.org");

        X500Name    subject = x500NameBld.build();

        PKCS10CertificationRequestBuilder requestBuilder = new JcaPKCS10CertificationRequestBuilder(subject, kp.getPublic());
                            
        PKCS10CertificationRequest req1 = requestBuilder.build(new JcaContentSignerBuilder(sigName).setProvider(provider).build(kp.getPrivate()));

        JcaPKCS10CertificationRequest req2 = new JcaPKCS10CertificationRequest(req1.getEncoded()).setProvider(provider);

        if (!req2.isSignatureValid(new JcaContentVerifierProviderBuilder().setProvider(provider).build(kp.getPublic())))
        {
            fail(sigName + ": Failed verify check.");
        }

        if (!Arrays.areEqual(req2.getPublicKey().getEncoded(), req1.getSubjectPublicKeyInfo().getEncoded()))
        {
            fail(keyName + ": Failed public key check.");
        }
    }

    private void generationTestX500Principal(int keySize, String keyName, String sigName, String provider)
        throws Exception
    {
        KeyPairGenerator kpg = KeyPairGenerator.getInstance(keyName, "BC");

        kpg.initialize(keySize);

        KeyPair kp = kpg.genKeyPair();


        X500NameBuilder x500NameBld = new X500NameBuilder(BCStyle.INSTANCE);

        x500NameBld.addRDN(BCStyle.C, "AU");
        x500NameBld.addRDN(BCStyle.O, "The Legion of the Bouncy Castle");
        x500NameBld.addRDN(BCStyle.L, "Melbourne");
        x500NameBld.addRDN(BCStyle.ST, "Victoria");
        x500NameBld.addRDN(BCStyle.EmailAddress, "feedback-crypto@bouncycastle.org");

        X500Name    subject = x500NameBld.build();

        PKCS10CertificationRequestBuilder requestBuilder = new JcaPKCS10CertificationRequestBuilder(new X500Principal(subject.getEncoded()), kp.getPublic());

        PKCS10CertificationRequest req1 = requestBuilder.build(new JcaContentSignerBuilder(sigName).setProvider(provider).build(kp.getPrivate()));

        JcaPKCS10CertificationRequest req2 = new JcaPKCS10CertificationRequest(req1.getEncoded()).setProvider(provider);

        if (!req2.isSignatureValid(new JcaContentVerifierProviderBuilder().setProvider(provider).build(kp.getPublic())))
        {
            fail(sigName + ": Failed verify check.");
        }

        if (!Arrays.areEqual(req2.getPublicKey().getEncoded(), req1.getSubjectPublicKeyInfo().getEncoded()))
        {
            fail(keyName + ": Failed public key check.");
        }

        if (!Arrays.areEqual(req2.getSubject().getEncoded(), req1.getSubject().getEncoded()))
        {
            fail(keyName + ": Failed subject key check.");
        }
    }

    /*
     * we generate a self signed certificate for the sake of testing - SHA224withECDSA
     */
    private void createECRequest(String algorithm, ASN1ObjectIdentifier algOid, ASN1ObjectIdentifier curveOid)
        throws Exception
    {
        ECNamedCurveParameterSpec spec = ECNamedCurveTable.getParameterSpec(curveOid.getId());
        KeyPairGenerator ecGen = KeyPairGenerator.getInstance("ECDSA", "BC");

        ecGen.initialize(spec);

        //
        // set up the keys
        //
        PrivateKey          privKey;
        PublicKey           pubKey;

        KeyPair pair = ecGen.generateKeyPair();

        privKey = pair.getPrivate();
        pubKey = pair.getPublic();

        ContentSigner signer = new JcaContentSignerBuilder(algorithm).setProvider(BC).build(privKey);

        PKCS10CertificationRequestBuilder reqBuilder = new JcaPKCS10CertificationRequestBuilder(new X500Name("CN=XXX"), pubKey);
        PKCS10CertificationRequest req = reqBuilder.build(signer);

        ContentVerifierProvider verifier = new JcaContentVerifierProviderBuilder().setProvider(BC).build(pubKey);

        if (!req.isSignatureValid(verifier))
        {
            fail("Failed verify check EC.");
        }

        req = new PKCS10CertificationRequest(req.getEncoded());
        if (!req.isSignatureValid(verifier))
        {
            fail("Failed verify check EC encoded.");
        }
        
        //
        // try with point compression turned off
        //
        ((ECPointEncoder)pubKey).setPointFormat("UNCOMPRESSED");

        reqBuilder = new JcaPKCS10CertificationRequestBuilder(new X500Name("CN=XXX"), pubKey);
        req = reqBuilder.build(signer);

        if (!req.isSignatureValid(verifier))
        {
            fail("Failed verify check EC uncompressed.");
        }
        
        req = new PKCS10CertificationRequest(req.getEncoded());
        if (!req.isSignatureValid(verifier))
        {
            fail("Failed verify check EC uncompressed encoded.");
        }
        
        if (!req.toASN1Structure().getSignatureAlgorithm().getAlgorithm().equals(algOid))
        {
            fail("ECDSA oid incorrect.");
        }
        
        if (req.toASN1Structure().getSignatureAlgorithm().getParameters() != null)
        {
            fail("ECDSA parameters incorrect.");
        }
        
        Signature sig = Signature.getInstance(algorithm, "BC");
        
        sig.initVerify(pubKey);
        
        sig.update(req.toASN1Structure().getCertificationRequestInfo().getEncoded());
        
        if (!sig.verify(req.toASN1Structure().getSignature().getOctets()))
        {
            fail("signature not mapped correctly.");
        }

        // empty tests
        try
        {
            new PKCS10CertificationRequest(new byte[0]);
            fail("no exception");
        }
        catch (IOException e)
        {
            isEquals("empty data passed to constructor", e.getMessage());
        }

        try
        {
            new PKCS10CertificationRequest((CertificationRequest)null);
            fail("no exception");
        }
        catch (NullPointerException e)
        {
            isEquals("certificationRequest cannot be null", e.getMessage());
        }
    }

    private void createECRequest(String algorithm, ASN1ObjectIdentifier algOid)
        throws Exception
    {
        X9ECParameters x9 = org.bouncycastle.asn1.x9.ECNamedCurveTable.getByName("secp521r1");
        ECCurve curve = x9.getCurve();
        ECParameterSpec spec = new ECParameterSpec(curve, x9.getG(), x9.getN(), x9.getH());

        ECPrivateKeySpec privKeySpec = new ECPrivateKeySpec(
            new BigInteger("5769183828869504557786041598510887460263120754767955773309066354712783118202294874205844512909370791582896372147797293913785865682804434049019366394746072023"), // d
            spec);

        ECPublicKeySpec pubKeySpec = new ECPublicKeySpec(
            curve.decodePoint(Hex.decode("02006BFDD2C9278B63C92D6624F151C9D7A822CC75BD983B17D25D74C26740380022D3D8FAF304781E416175EADF4ED6E2B47142D2454A7AC7801DD803CF44A4D1F0AC")), // Q
            spec);

        //
        // set up the keys
        //
        PrivateKey          privKey;
        PublicKey           pubKey;

        KeyFactory     fact = KeyFactory.getInstance("ECDSA", "BC");

        privKey = fact.generatePrivate(privKeySpec);
        pubKey = fact.generatePublic(pubKeySpec);

        PKCS10CertificationRequest req = new JcaPKCS10CertificationRequestBuilder(
                        new X500Name("CN=XXX"), pubKey).build(new JcaContentSignerBuilder(algorithm).setProvider(BC).build(privKey));
        if (!req.isSignatureValid(new JcaContentVerifierProviderBuilder().setProvider(BC).build(pubKey)))
        {
            fail("Failed verify check EC.");
        }

        req = new PKCS10CertificationRequest(req.getEncoded());
        if (!req.isSignatureValid(new JcaContentVerifierProviderBuilder().setProvider(BC).build(pubKey)))
        {
            fail("Failed verify check EC encoded.");
        }

        //
        // try with point compression turned off
        //
        ((ECPointEncoder)pubKey).setPointFormat("UNCOMPRESSED");

        req = new JcaPKCS10CertificationRequestBuilder(
                        new X500Name("CN=XXX"), pubKey).build(new JcaContentSignerBuilder(algorithm).setProvider(BC).build(privKey));
        if (!req.isSignatureValid(new JcaContentVerifierProviderBuilder().setProvider(BC).build(pubKey)))
        {
            fail("Failed verify check EC uncompressed.");
        }

        JcaPKCS10CertificationRequest jcaReq = new JcaPKCS10CertificationRequest(new PKCS10CertificationRequest(req.getEncoded()));
        if (!jcaReq.isSignatureValid(new JcaContentVerifierProviderBuilder().setProvider(BC).build(jcaReq.getPublicKey())))
        {
            fail("Failed verify check EC uncompressed encoded.");
        }

        if (!jcaReq.getSignatureAlgorithm().getAlgorithm().equals(algOid))
        {
            fail("ECDSA oid incorrect.");
        }

        if (jcaReq.getSignatureAlgorithm().getParameters() != null)
        {
            fail("ECDSA parameters incorrect.");
        }

        Signature sig = Signature.getInstance(algorithm, BC);

        sig.initVerify(pubKey);

        sig.update(req.toASN1Structure().getCertificationRequestInfo().getEncoded());

        if (!sig.verify(req.getSignature()))
        {
            fail("signature not mapped correctly.");
        }
    }

    private void createECGOSTRequest()
        throws Exception
    {
        String           algorithm = "GOST3411withECGOST3410";
        KeyPairGenerator ecGostKpg = KeyPairGenerator.getInstance("ECGOST3410", "BC");

        ecGostKpg.initialize(ECGOST3410NamedCurveTable.getParameterSpec("GostR3410-2001-CryptoPro-A"), new SecureRandom());

        //
        // set up the keys
        //
        KeyPair             pair = ecGostKpg.generateKeyPair();
        PrivateKey          privKey = pair.getPrivate();
        PublicKey           pubKey = pair.getPublic();

        PKCS10CertificationRequest req = new JcaPKCS10CertificationRequestBuilder(
                        new X500Name("CN=XXX"), pubKey).build(new JcaContentSignerBuilder(algorithm).setProvider(BC).build(privKey));
        if (!req.isSignatureValid(new JcaContentVerifierProviderBuilder().setProvider(BC).build(pubKey)))
        {
            fail("Failed verify check EC.");
        }

        req = new PKCS10CertificationRequest(req.getEncoded());
        if (!req.isSignatureValid(new JcaContentVerifierProviderBuilder().setProvider(BC).build(pubKey)))
        {
            fail("Failed verify check EC encoded.");
        }

        if (!req.getSignatureAlgorithm().getAlgorithm().equals(CryptoProObjectIdentifiers.gostR3411_94_with_gostR3410_2001))
        {
            fail("ECGOST oid incorrect.");
        }

        if (req.getSignatureAlgorithm().getParameters() != null)
        {
            fail("ECGOST parameters incorrect.");
        }

        Signature sig = Signature.getInstance(algorithm, "BC");

        sig.initVerify(pubKey);

        sig.update(req.toASN1Structure().getCertificationRequestInfo().getEncoded());

        if (!sig.verify(req.getSignature()))
        {
            fail("signature not mapped correctly.");
        }
    }

    private void createPSSTest(String algorithm)
        throws Exception
    {
        RSAPublicKeySpec pubKeySpec = new RSAPublicKeySpec(
            new BigInteger("a56e4a0e701017589a5187dc7ea841d156f2ec0e36ad52a44dfeb1e61f7ad991d8c51056ffedb162b4c0f283a12a88a394dff526ab7291cbb307ceabfce0b1dfd5cd9508096d5b2b8b6df5d671ef6377c0921cb23c270a70e2598e6ff89d19f105acc2d3f0cb35f29280e1386b6f64c4ef22e1e1f20d0ce8cffb2249bd9a2137",16),
            new BigInteger("010001",16));

        RSAPrivateCrtKeySpec privKeySpec = new RSAPrivateCrtKeySpec(
            new BigInteger("a56e4a0e701017589a5187dc7ea841d156f2ec0e36ad52a44dfeb1e61f7ad991d8c51056ffedb162b4c0f283a12a88a394dff526ab7291cbb307ceabfce0b1dfd5cd9508096d5b2b8b6df5d671ef6377c0921cb23c270a70e2598e6ff89d19f105acc2d3f0cb35f29280e1386b6f64c4ef22e1e1f20d0ce8cffb2249bd9a2137",16),
            new BigInteger("010001",16),
            new BigInteger("33a5042a90b27d4f5451ca9bbbd0b44771a101af884340aef9885f2a4bbe92e894a724ac3c568c8f97853ad07c0266c8c6a3ca0929f1e8f11231884429fc4d9ae55fee896a10ce707c3ed7e734e44727a39574501a532683109c2abacaba283c31b4bd2f53c3ee37e352cee34f9e503bd80c0622ad79c6dcee883547c6a3b325",16),
            new BigInteger("e7e8942720a877517273a356053ea2a1bc0c94aa72d55c6e86296b2dfc967948c0a72cbccca7eacb35706e09a1df55a1535bd9b3cc34160b3b6dcd3eda8e6443",16),
            new BigInteger("b69dca1cf7d4d7ec81e75b90fcca874abcde123fd2700180aa90479b6e48de8d67ed24f9f19d85ba275874f542cd20dc723e6963364a1f9425452b269a6799fd",16),
            new BigInteger("28fa13938655be1f8a159cbaca5a72ea190c30089e19cd274a556f36c4f6e19f554b34c077790427bbdd8dd3ede2448328f385d81b30e8e43b2fffa027861979",16),
            new BigInteger("1a8b38f398fa712049898d7fb79ee0a77668791299cdfa09efc0e507acb21ed74301ef5bfd48be455eaeb6e1678255827580a8e4e8e14151d1510a82a3f2e729",16),
            new BigInteger("27156aba4126d24a81f3a528cbfb27f56886f840a9f6e86e17a44b94fe9319584b8e22fdde1e5a2e3bd8aa5ba8d8584194eb2190acf832b847f13a3d24a79f4d",16));

        KeyFactory  fact = KeyFactory.getInstance("RSA", "BC");

        PrivateKey privKey = fact.generatePrivate(privKeySpec);
        PublicKey pubKey = fact.generatePublic(pubKeySpec);

        PKCS10CertificationRequest req = new JcaPKCS10CertificationRequestBuilder(
                        new X500Name("CN=XXX"), pubKey).build(new JcaContentSignerBuilder(algorithm).setProvider(BC).build(privKey));
        if (!req.isSignatureValid(new JcaContentVerifierProviderBuilder().setProvider(BC).build(pubKey)))
        {
            fail("Failed verify check PSS.");
        }

        JcaPKCS10CertificationRequest jcaReq = new JcaPKCS10CertificationRequest(req.getEncoded()).setProvider(BC);
        if (!jcaReq.isSignatureValid(new JcaContentVerifierProviderBuilder().setProvider(BC).build(jcaReq.getPublicKey())))
        {
            fail("Failed verify check PSS encoded.");
        }

        if (!jcaReq.getSignatureAlgorithm().getAlgorithm().equals(PKCSObjectIdentifiers.id_RSASSA_PSS))
        {
            fail("PSS oid incorrect.");
        }

        if (jcaReq.getSignatureAlgorithm().getParameters() == null)
        {
            fail("PSS parameters incorrect.");
        }

        Signature sig = Signature.getInstance(algorithm, "BC");

        sig.initVerify(pubKey);

        sig.update(jcaReq.toASN1Structure().getCertificationRequestInfo().getEncoded());

        if (!sig.verify(req.getSignature()))
        {
            fail("signature not mapped correctly.");
        }
    }

     // previous code found to cause a NullPointerException
    private void nullPointerTest()
        throws Exception
    {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA", "BC");
        keyGen.initialize(1024, new SecureRandom());
        KeyPair pair = keyGen.generateKeyPair();
        JcaX509ExtensionUtils extUtils = new JcaX509ExtensionUtils();

        Extension[] ext = new Extension[] {
            new Extension(Extension.basicConstraints, true, new DEROctetString(new BasicConstraints(true))),
            new Extension(Extension.keyUsage, true, new DEROctetString(new KeyUsage(KeyUsage.keyCertSign | KeyUsage.cRLSign))),
            new Extension(Extension.subjectKeyIdentifier, false, new DEROctetString(extUtils.createSubjectKeyIdentifier(pair.getPublic())))
        };

        PKCS10CertificationRequest p1 = new JcaPKCS10CertificationRequestBuilder(
            new X500Name("cn=csr"),
            pair.getPublic())
            .addAttribute(PKCSObjectIdentifiers.pkcs_9_at_extensionRequest, new Extensions(ext))
            .build(new JcaContentSignerBuilder("SHA1withRSA").setProvider(BC).build(pair.getPrivate()));
        PKCS10CertificationRequest p2 = new JcaPKCS10CertificationRequestBuilder(
            new X500Name("cn=csr"),
            pair.getPublic())
            .addAttribute(PKCSObjectIdentifiers.pkcs_9_at_extensionRequest, new Extensions(ext))
            .build(new JcaContentSignerBuilder("SHA1withRSA").setProvider(BC).build(pair.getPrivate()));

        if (!p1.equals(p2))
        {
            fail("cert request comparison failed");
        }

        Attribute[] attr1 = p1.getAttributes();
        Attribute[] attr2 = p2.getAttributes();

        checkAttrs(1, attr1, attr2);

        attr1 = p1.getAttributes(PKCSObjectIdentifiers.pkcs_9_at_extensionRequest);
        attr2 = p2.getAttributes(PKCSObjectIdentifiers.pkcs_9_at_extensionRequest);

        checkAttrs(1, attr1, attr2);
    }

    private void checkAttrs(int expectedLength, Attribute[] attr1, Attribute[] attr2)
    {
        if (expectedLength != attr1.length)
        {
            fail("expected length mismatch");
        }

        if (attr1.length != attr2.length)
        {
            fail("atrribute length mismatch");
        }

        for (int i = 0; i != attr1.length; i++)
        {
            if (!attr1[i].equals(attr2[i]))
            {
                fail("atrribute mismatch");
            }
        }
    }

    public void performTest()
        throws Exception
    {
        generationTest(512, "RSA", "SHA1withRSA", "BC");
        generationTestX500Principal(512, "RSA", "SHA1withRSA", "BC");
        generationTest(512, "GOST3410", "GOST3411withGOST3410", "BC");
        
        if (Security.getProvider("SunRsaSign") != null)
        {
            generationTest(512, "RSA", "SHA1withRSA", "SunRsaSign"); 
        }
        
        // elliptic curve GOST A parameter set
        JcaPKCS10CertificationRequest req = new JcaPKCS10CertificationRequest(gost3410EC_A).setProvider(BC);
        if (!req.isSignatureValid(new JcaContentVerifierProviderBuilder().setProvider(BC).build(req.getPublicKey())))
        {
            fail("Failed verify check gost3410EC_A.");
        }

        // elliptic curve GOST B parameter set
        req = new JcaPKCS10CertificationRequest(gost3410EC_B).setProvider(BC);
        if (!req.isSignatureValid(new JcaContentVerifierProviderBuilder().setProvider(BC).build(req.getPublicKey())))
        {
            fail("Failed verify check gost3410EC_B.");
        }

        // elliptic curve GOST C parameter set
        req = new JcaPKCS10CertificationRequest(gost3410EC_C).setProvider(BC);
        if (!req.isSignatureValid(new JcaContentVerifierProviderBuilder().setProvider(BC).build(req.getPublicKey())))
        {
            fail("Failed verify check gost3410EC_C.");
        }
        
        // elliptic curve GOST ExA parameter set
        req = new JcaPKCS10CertificationRequest(gost3410EC_ExA).setProvider(BC);
        if (!req.isSignatureValid(new JcaContentVerifierProviderBuilder().setProvider(BC).build(req.getPublicKey())))
        {
            fail("Failed verify check gost3410EC_ExA.");
        }

        // elliptic curve GOST ExB parameter set
        req = new JcaPKCS10CertificationRequest(gost3410EC_ExB).setProvider(BC);
        if (!req.isSignatureValid(new JcaContentVerifierProviderBuilder().setProvider(BC).build(req.getPublicKey())))
        {
            fail("Failed verify check gost3410EC_ExA.");
        }

        // elliptic curve openSSL
        KeyPairGenerator g = KeyPairGenerator.getInstance("ECDSA", "BC");

        X9ECParameters x9 = org.bouncycastle.asn1.x9.ECNamedCurveTable.getByName("prime239v1");
        ECCurve curve = x9.getCurve();
        ECParameterSpec ecSpec = new ECParameterSpec(curve, x9.getG(), x9.getN(), x9.getH());

        g.initialize(ecSpec, new SecureRandom());

        KeyPair kp = g.generateKeyPair();

        req = new JcaPKCS10CertificationRequest(new JcaPKCS10CertificationRequestBuilder(
               new X500Name("CN=XXX"), kp.getPublic()).build(new JcaContentSignerBuilder( "ECDSAWITHSHA1").setProvider(BC).build(kp.getPrivate())));
        if (!req.isSignatureValid(new JcaContentVerifierProviderBuilder().setProvider(BC).build(req.getPublicKey())))
        {
            fail("Failed verify check EC.");
        }
        
        createECRequest("SHA1withECDSA", X9ObjectIdentifiers.ecdsa_with_SHA1);
        createECRequest("SHA224withECDSA", X9ObjectIdentifiers.ecdsa_with_SHA224);
        createECRequest("SHA256withECDSA", X9ObjectIdentifiers.ecdsa_with_SHA256);
        createECRequest("SHA384withECDSA", X9ObjectIdentifiers.ecdsa_with_SHA384);
        createECRequest("SHA512withECDSA", X9ObjectIdentifiers.ecdsa_with_SHA512);

        createECRequest("SHA1withECDSA", X9ObjectIdentifiers.ecdsa_with_SHA1, new ASN1ObjectIdentifier("1.3.132.0.34"));

        createECGOSTRequest();

        createPSSTest("SHA1withRSAandMGF1");
        createPSSTest("SHA224withRSAandMGF1");
        createPSSTest("SHA256withRSAandMGF1");
        createPSSTest("SHA384withRSAandMGF1");

        nullPointerTest();
    }

    public static void main(
        String[]    args)
    {
        Security.addProvider(new BouncyCastleProvider());

        runTest(new PKCS10Test());
    }
}
