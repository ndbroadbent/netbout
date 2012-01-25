/**
 * Copyright (c) 2009-2011, netBout.com
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are PROHIBITED without prior written permission from
 * the author. This product may NOT be used anywhere and on any computer
 * except the server platform of netBout Inc. located at www.netbout.com.
 * Federal copyright law prohibits unauthorized reproduction by any means
 * and imposes fines up to $25,000 for violation. If you received
 * this code occasionally and without intent to use it, please report this
 * incident to the author by email.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 */
package com.netbout.rest.bumper;

import com.netbout.spi.Bout;
import com.netbout.spi.Identity;
import com.netbout.spi.Urn;
import com.netbout.spi.cpa.Farm;
import com.netbout.spi.cpa.IdentityAware;
import com.netbout.spi.cpa.Operation;
import java.net.URI;
import java.net.URL;
import java.util.List;
import javax.ws.rs.core.UriBuilder;

/**
 * Bumper farm.
 *
 * @author Yegor Bugayenko (yegor@netbout.com)
 * @version $Id$
 */
@Farm
public final class BumperFarmMocker implements IdentityAware {

    /**
     * URL of XSD.
     */
    private static URL xsd;

    /**
     * Me.
     */
    private transient Identity identity;

    /**
     * Inform about base URI.
     */
    public static void setBaseUri(final URI uri) {
        try {
            BumperFarmMocker.xsd = UriBuilder.fromUri(uri)
                .path("/bumper/ns.xsd")
                .build()
                .toURL();
        } catch (java.net.MalformedURLException ex) {
            throw new IllegalArgumentException(ex);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void init(final Identity idnt) {
        this.identity = idnt;
    }

    /**
     * Resolve namespace.
     * @param namespace The namespace
     * @return Its URI
     */
    @Operation("resolve-xml-namespace")
    public URL resolveXmlNamespace(final Urn namespace) {
        URL url = null;
        if (namespace.equals(Urn.create("urn:test:bumper:ns"))) {
            url = BumperFarmMocker.xsd;
        }
        return url;
    }

    /**
     * Evaluate predicate.
     * @param bout Number of bout
     * @param msg Number of message
     * @param name Name of predicate
     * @param args Arguments
     * @return Value or NULL if name is unknown for us
     */
    @Operation("evaluate-predicate")
    public String evaluatePredicate(final Long bout, final Long msg,
        final Urn name, final List args) {
        String response = null;
        if ("urn:test:bumper:what-is-your-name".equals(name.toString())) {
            response = "bumper";
        }
        return response;
    }

    /**
     * Somebody was just invited to the bout.
     * @param number Bout where it is happening
     */
    @Operation("just-invited")
    public void justInvited(final Long number) {
        Bout bout;
        try {
            bout = this.identity.bout(number);
        } catch (com.netbout.spi.BoutNotFoundException ex) {
            throw new IllegalArgumentException(ex);
        }
        bout.confirm();
    }

}
