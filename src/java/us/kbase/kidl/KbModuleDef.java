package us.kbase.kidl;

import java.util.Map;

/** Element of module declaration - Typedef or Funcdef.
 * @author gaprice@lbl.gov
 *
 */
public interface KbModuleDef extends KbModuleComp{

	public String getName();

	public String getComment();

	public Map<?, ?> getData();

	public KbAnnotations getAnnotations();

}