/**
 * Implements stemming method used for normalizing both documents and queries
 */
public class ImprovedIndexingEngine extends IndexingEngine{
	ImprovedIndexingEngine(String docsFile) throws Exception {
		super(docsFile);
		// http://snowball.tartarus.org/algorithms/porter/stemmer.html
		// Porter Stemmer - step 1
		_normalizingStrings.put("sses\\b", "ss");
		_normalizingStrings.put("ies\\b", "i");
		_normalizingStrings.put("s\\b", "");
	
		// //Porter Stemmer - step 2
		_normalizingStrings.put("eed\\b", "ee");
		_normalizingStrings.put("ed\\b", "");
		_normalizingStrings.put("ing\\b", "");
	
		_normalizingStrings.put("at\\b", "ate");
		_normalizingStrings.put("bl\\b", "ble");
		_normalizingStrings.put("iz\\b", "ize");
	
		_normalizingStrings.put("ational\\b", "ate"); // relational -> relate
		_normalizingStrings.put("tional\\b", "tion"); // conditional -> condition
		_normalizingStrings.put("enci\\b", "ence"); // valenci -> valence
		_normalizingStrings.put("anci\\b", "ance"); // hesitanci -> hesitance
		_normalizingStrings.put("izer\\b", "ize"); // digitizer -> digitize
		_normalizingStrings.put("abli\\b", "able"); // conformabli -> conformable
		_normalizingStrings.put("alli\\b", "al"); // radicalli -> radical
		_normalizingStrings.put("entli\\b", "ent"); // differentli -> different
		_normalizingStrings.put("eli\\b", "e"); // vileli - > vile
		_normalizingStrings.put("ousli\\b", "ous"); // analogousli -> analogous
		_normalizingStrings.put("ization\\b", "ize"); // vietnamization -> vietnamize
		_normalizingStrings.put("ation\\b", "ate"); // predication -> predicate
		_normalizingStrings.put("ator\\b", "ate"); // operator -> operate
		_normalizingStrings.put("alism\\b", "al"); // feudalism -> feudal
		_normalizingStrings.put("iveness\\b", "ive"); // decisiveness -> decisive
		_normalizingStrings.put("fulness\\b", "ful"); // hopefulness -> hopeful
		_normalizingStrings.put("ousness\\b", "ous"); // callousness -> callous
		_normalizingStrings.put("aliti\\b", "al"); // formaliti -> formal
		_normalizingStrings.put("iviti\\b", "ive"); // sensitiviti -> sensitive
		_normalizingStrings.put("biliti\\b", "ble"); // sensibiliti -> sensible
	}
}
