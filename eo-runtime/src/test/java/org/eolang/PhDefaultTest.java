/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016-2021 Yegor Bugayenko
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.eolang;

import EOorg.EOeolang.EOtxt.EOsprintf;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link PhDefault}.
 *
 * @since 0.1
 */
public final class PhDefaultTest {

    @Test
    public void makesCopy() {
        final Phi num = new Data.ToPhi(42L);
        final Phi parent = new EOsprintf(new PhEta());
        final String data = "Hello, world!";
        final Phi phi = new PhDefaultTest.Foo(parent, data);
        phi.attr(0).put(num);
        final Phi copy = phi.copy(new PhEta());
        MatcherAssert.assertThat(
            new Dataized(copy).take(String.class),
            Matchers.equalTo(data)
        );
        MatcherAssert.assertThat(
            phi.attr("x").get().attr("Δ"),
            Matchers.notNullValue()
        );
    }

    @Test
    public void setsFreeAttributeOnlyOnce() {
        final Phi num = new Data.ToPhi(42L);
        final Phi phi = new PhDefaultTest.Foo(new PhEta());
        phi.attr(0).put(num);
        Assertions.assertThrows(
            Attr.ReadOnlyException.class,
            () -> phi.attr(0).put(num)
        );
    }

    @Test
    public void takesRhoFromAttribute() {
        final Phi phi = new PhDefaultTest.Kid(new Data.ToPhi(0L));
        MatcherAssert.assertThat(
            new Dataized(phi.attr("φ").get().attr("ρ").get()).take(Long.class),
            Matchers.equalTo(1L)
        );
    }

    @Test
    public void changesRhoOnCopy() {
        final Phi foo = new Foo(new PhEta());
        final Phi kid = foo.attr("kid").get();
        final Phi copy = kid.copy(new PhEta());
        MatcherAssert.assertThat(
            copy.attr("ρ").get(),
            Matchers.not(Matchers.equalTo(foo))
        );
    }

    @Test
    public void getsRhoFromPhi() {
        final Phi first = new First(new PhEta());
        MatcherAssert.assertThat(
            new Dataized(first).take(Long.class),
            Matchers.equalTo(1L)
        );
    }

    @Test
    public void printsEndlessRecursionObject() {
        final Phi phi = new PhDefaultTest.EndlessRecursion(new PhEta());
        PhDefaultTest.EndlessRecursion.count = 2;
        MatcherAssert.assertThat(
            new Dataized(phi).take(Long.class),
            Matchers.equalTo(0L)
        );
    }

    public static class Foo extends PhDefault {
         public Foo(final Phi sigma) {
             this(sigma, new Object());
         }
         public Foo(final Phi sigma, final Object data) {
             super(sigma);
             this.add("x", new AtFree());
             this.add("kid", new AtBound(new AtLambda(
                 this, PhDefaultTest.Kid::new
             )));
             this.add("φ", new AtBound(new AtLambda(
                 this, self -> new Data.ToPhi(data)
             )));
        }
    }

    public static class Kid extends PhDefault {
        public Kid(final Phi sigma) {
            super(sigma);
            this.add("z", new AtFree());
            this.add("φ", new AtBound(new AtLambda(
                this, self -> new EOsprintf(new Data.ToPhi(1L))
            )));
        }
    }

    public static class First extends PhDefault {
        public First(final Phi sigma) {
            super(sigma);
            this.add("a", new AtFree(new Data.ToPhi(1L)));
            this.add("φ", new AtBound(new AtLambda(
                this, PhDefaultTest.Second::new
            )));
        }
    }

    public static class Second extends PhDefault {
        public Second(final Phi sigma) {
            super(sigma);
            this.add("φ", new AtBound(new AtLambda(
                this, self -> self.attr("ρ").get().attr("a").get()
            )));
        }
    }

    public static class EndlessRecursion extends PhDefault {
        public static int count;
        public EndlessRecursion(final Phi sigma) {
            super(sigma);
            this.add("φ", new AtBound(new AtLambda(
                this, self -> {
                    --PhDefaultTest.EndlessRecursion.count;
                    if (PhDefaultTest.EndlessRecursion.count <= 0) {
                        return new Data.ToPhi(0L);
                    }
                System.out.println("ret");
                    Phi ret = new PhDefaultTest.EndlessRecursion(self);
                    return ret;
//                    return new PhCopy(new PhDefaultTest.EndlessRecursion(self), self);
                }
            )));
        }
    }

}
