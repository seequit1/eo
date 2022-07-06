package EOorg.EOeolang.EOmath;

import org.eolang.AtComposite;
import org.eolang.Data;
import org.eolang.Param;
import org.eolang.PhDefault;
import org.eolang.Phi;
import org.eolang.XmirObject;

/**
 * Cos.
 * @since 1.0
 */
@XmirObject(oname = "angle.cos")
public class EOangle$EOcos extends PhDefault {

    /**
     * Ctor.
     * @param sigma Sigma
     */
    public EOangle$EOcos(final Phi sigma) {
        super(sigma);
        this.add("φ", new AtComposite(this, rho -> new Data.ToPhi(
            Math.cos(new Param(rho.attr("ρ").get(), "f").strong(Double.class))
        )));
    }
}
