/**
 * Copyright (c) 2009-2011, NetBout.com
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met: 1) Redistributions of source code must retain the above
 * copyright notice, this list of conditions and the following
 * disclaimer. 2) Redistributions in binary form must reproduce the above
 * copyright notice, this list of conditions and the following
 * disclaimer in the documentation and/or other materials provided
 * with the distribution. 3) Neither the name of the NetBout.com nor
 * the names of its contributors may be used to endorse or promote
 * products derived from this software without specific prior written
 * permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT
 * NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL
 * THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.netbout.spi;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.SortedMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

/**
 * Mocker of {@link Identity}.
 *
 * @author Yegor Bugayenko (yegor@netbout.com)
 * @version $Id$
 */
public final class IdentityMocker {

    /**
     * Mocked identity.
     */
    private final Identity identity = Mockito.mock(Identity.class);

    /**
     * Aliases.
     */
    private final List<String> aliases = new ArrayList<String>();

    /**
     * All bouts.
     */
    private final SortedMap<Long, Bout> bouts =
        new ConcurrentSkipListMap<Long, Bout>();

    /**
     * Inboxes.
     */
    private final Map<String, Long[]> inboxes =
        new ConcurrentHashMap<String, Long[]>();

    /**
     * Public ctor.
     */
    public IdentityMocker() {
        this.namedAs(new UrnMocker().mock());
        this.belongsTo("http://localhost/set-by-IdentityMocker");
        this.withPhoto("http://localhost/set-by-IdentityMocker.png");
        Mockito.doAnswer(
            new Answer() {
                @Override
                public Object answer(final InvocationOnMock invocation) {
                    return new HashSet(IdentityMocker.this.aliases);
                }
            }
        ).when(this.identity).aliases();
        Mockito.doAnswer(
            new Answer() {
                @Override
                public Object answer(final InvocationOnMock invocation) {
                    final Long num = Math.abs(new Random().nextLong());
                    final Bout bout = new BoutMocker().withNumber(num).mock();
                    IdentityMocker.this.withBout(num, bout);
                    return bout;
                }
            }
        ).when(this.identity).start();
        Mockito.doAnswer(
            new Answer() {
                @Override
                public Object answer(final InvocationOnMock invocation) {
                    final List<Bout> inbox = new ArrayList<Bout>(
                        IdentityMocker.this.bouts.values()
                    );
                    Collections.reverse(inbox);
                    return inbox;
                }
            }
        ).when(this.identity).inbox(Mockito.anyString());
        try {
            Mockito.doAnswer(
                new Answer() {
                    @Override
                    public Object answer(final InvocationOnMock invocation) {
                        final Long num = (Long) invocation.getArguments()[0];
                        final Bout bout = IdentityMocker.this.bouts.get(num);
                        if (bout == null) {
                            throw new IllegalArgumentException(
                                "call #withBout() first on IdentityMocker"
                            );
                        }
                        return bout;
                    }
                }
            ).when(this.identity).bout(Mockito.anyLong());
        } catch (com.netbout.spi.BoutNotFoundException ex) {
            throw new IllegalArgumentException(ex);
        }
    }

    /**
     * This is the name of identity.
     * @param The name of it
     * @return This object
     */
    public IdentityMocker namedAs(final String name) {
        return this.namedAs(Urn.create(name));
    }

    /**
     * This is the name of identity.
     * @param The name of it
     * @return This object
     */
    public IdentityMocker namedAs(final Urn name) {
        Mockito.doReturn(name).when(this.identity).name();
        return this;
    }

    /**
     * This is the user of identity, which it belongs to.
     * @param The name of user
     * @return This object
     */
    public IdentityMocker belongsTo(final String name) {
        try {
            return this.belongsTo(new URL(name));
        } catch (java.net.MalformedURLException ex) {
            throw new IllegalArgumentException(ex);
        }
    }

    /**
     * This is the user of identity, which it belongs to.
     * @param The name of user
     * @return This object
     */
    public IdentityMocker belongsTo(final URL name) {
        Mockito.doReturn(name).when(this.identity).authority();
        return this;
    }

    /**
     * With this alias.
     * @param alias The alias
     * @return This object
     */
    public IdentityMocker withAlias(final String alias) {
        this.aliases.add(0, alias);
        return this;
    }

    /**
     * With this photo.
     * @param photo The photo
     * @return This object
     */
    public IdentityMocker withPhoto(final String photo) {
        try {
            Mockito.doReturn(new URL(photo)).when(this.identity).photo();
        } catch (java.net.MalformedURLException ex) {
            throw new IllegalArgumentException(ex);
        }
        return this;
    }

    /**
     * With this bout on board.
     * @param num Number of it
     * @param bout The bout
     * @return This object
     * @throws BoutNotFoundException If some problem
     */
    public IdentityMocker withBout(final Long num, final Bout bout) {
        this.bouts.put(num, bout);
        return this;
    }

    /**
     * With this inbox.
     * @param query The query
     * @param nums List of bout numbers to return
     * @return This object
     */
    public IdentityMocker withInbox(final String query, final Long[] nums) {
        Mockito.doAnswer(
            new Answer() {
                @Override
                public Object answer(final InvocationOnMock invocation) {
                    final List<Bout> inbox = new ArrayList<Bout>();
                    for (Long num : nums) {
                        inbox.add(IdentityMocker.this.bouts.get(num));
                    }
                    return inbox;
                }
            }
        ).when(this.identity).inbox(query);
        return this;
    }

    /**
     * Mock it.
     * @return Mocked identity
     */
    public Identity mock() {
        if (this.bouts.isEmpty()) {
            final Long num = Math.abs(new Random().nextLong());
            this.withBout(num, new BoutMocker().withNumber(num).mock());
        }
        if (this.aliases.isEmpty()) {
            this.withAlias("test identity alias set by IdentityMocker");
        }
        return this.identity;
    }

}