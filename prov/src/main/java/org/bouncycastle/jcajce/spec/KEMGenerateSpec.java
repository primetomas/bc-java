package org.bouncycastle.jcajce.spec;

import java.security.PublicKey;
import java.security.spec.AlgorithmParameterSpec;

import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x9.X9ObjectIdentifiers;
import org.bouncycastle.util.Arrays;

public class KEMGenerateSpec
    extends KEMKDFSpec
    implements AlgorithmParameterSpec
{
    private static final byte[] EMPTY_OTHER_INFO = new byte[0];
    private static AlgorithmIdentifier DefKdf = new AlgorithmIdentifier(X9ObjectIdentifiers.id_kdf_kdf3, new AlgorithmIdentifier(NISTObjectIdentifiers.id_sha256));

    /**
     * Builder class for creating a KTSParameterSpec.
     */
    public static final class Builder
    {
        private final PublicKey publicKey;
        private final String algorithmName;
        private final int keySizeInBits;

        private AlgorithmIdentifier kdfAlgorithm;
        private byte[] otherInfo;

        /**
         * Basic builder.
         *
         * @param publicKey the public key to use for encapsulation/secret generation.
         * @param keyAlgorithmName the algorithm name for the secret key we want to generate.
         * @param keySizeInBits the size of the wrapping key we want to produce in bits.
         */
        public Builder(PublicKey publicKey, String keyAlgorithmName, int keySizeInBits)
        {
            this.publicKey = publicKey;
            this.algorithmName = keyAlgorithmName;
            this.keySizeInBits = keySizeInBits;
            this.kdfAlgorithm = new AlgorithmIdentifier(X9ObjectIdentifiers.id_kdf_kdf3, new AlgorithmIdentifier(NISTObjectIdentifiers.id_sha256));
            this.otherInfo = EMPTY_OTHER_INFO;
        }

        /**
         * Use the shared secret directly for key wrap generation.
         *
         * @return the current Builder instance.
         */
        public Builder withNoKdf()
        {
            this.kdfAlgorithm = null;

            return this;
        }

        /**
         * Set the KDF algorithm and digest algorithm for wrap key generation. The default KDF is X9.44 KDF-3, also
         * known as the NIST concatenation KDF.
         *
         * @param kdfAlgorithm the KDF algorithm to apply.
         * @return the current Builder instance.
         */
        public Builder withKdfAlgorithm(AlgorithmIdentifier kdfAlgorithm)
        {
            this.kdfAlgorithm = kdfAlgorithm;

            return this;
        }

        /**
         * Set the OtherInfo to use with the KDF. The default OtherInfo is a zero length byte[].
         *
         * @param otherInfo the other info to use.
         * @return the current Builder instance.
         */
        public Builder withOtherInfo(byte[] otherInfo)
        {
            this.otherInfo = (otherInfo == null) ? EMPTY_OTHER_INFO : Arrays.clone(otherInfo);

            return this;
        }

        /**
         * Build the new parameter spec.
         *
         * @return a new parameter spec configured according to the builder state.
         */
        public KEMGenerateSpec build()
        {
            return new KEMGenerateSpec(publicKey, algorithmName, keySizeInBits, kdfAlgorithm, otherInfo);
        }
    }

    private final PublicKey publicKey;

    private KEMGenerateSpec(PublicKey publicKey, String keyAlgorithmName, int keySizeInBits, AlgorithmIdentifier kdfAlgorithm, byte[] otherInfo)
    {
        super(kdfAlgorithm, otherInfo, keyAlgorithmName, keySizeInBits);

        this.publicKey = publicKey;
    }

    public KEMGenerateSpec(PublicKey publicKey, String keyAlgorithmName)
    {
        this(publicKey, keyAlgorithmName, 256, DefKdf, EMPTY_OTHER_INFO);
    }

    public KEMGenerateSpec(PublicKey publicKey, String keyAlgorithmName, int keySizeInBits)
    {
        this(publicKey, keyAlgorithmName, keySizeInBits, DefKdf, EMPTY_OTHER_INFO);
    }

    public PublicKey getPublicKey()
    {
        return publicKey;
    }
}
