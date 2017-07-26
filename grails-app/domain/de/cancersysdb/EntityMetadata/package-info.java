/**
 * This package holds classes for Metadata which can be attached to Patients.
 * It follows a two level approach
 * <li>First level describes the Document the ClinicalInformation ImportInfo is from and if its restricted.</li>
 * <li>The second level holds simple Information snippets (info). They are like a key value store.</li>
 * location field adresses the Position in the informations Structure of the imported document.
 * <p>
 * In general its meant to be a simple and lean way to Put XML into the Database.
 * An XML tree will be deconstructed to lines (info object) the Structiral position as Xpath will be saved in the location field.
 */

package de.cancersysdb.EntityMetadata;