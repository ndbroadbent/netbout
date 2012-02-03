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
package com.netbout.inf.predicates;

import com.netbout.inf.Meta;
import com.netbout.inf.Msg;
import com.netbout.inf.Predicate;
import java.util.List;

/**
 * Allows only messages that unbundle on the specified bout number.
 *
 * @author Yegor Bugayenko (yegor@netbout.com)
 * @version $Id$
 */
@Meta(name = "unbundled")
public final class UnbundledPred extends AbstractVarargPred {

    /**
     * Expected marker.
     */
    private transient String expected;

    /**
     * Public ctor.
     * @param args The arguments
     */
    public UnbundledPred(final List<Predicate> args) {
        super(args);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object evaluate(final Msg msg, final int pos) {
        final Long bout = Long.valueOf(
            this.arg(0).evaluate(msg, pos).toString()
        );
        final String marker = msg.<String>get(BundledPred.BUNDLE);
        boolean allow;
        if (msg.bout().equals(bout)) {
            this.expected = marker;
            allow = false;
        } else {
            allow = marker.equals(this.expected);
        }
        return allow;
    }

}