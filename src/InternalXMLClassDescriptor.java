import org.exolab.castor.mapping.ClassDescriptor;
import org.exolab.castor.mapping.FieldDescriptor;
import org.exolab.castor.xml.Introspector;
import org.exolab.castor.xml.NodeType;
import org.exolab.castor.xml.TypeValidator;
import org.exolab.castor.xml.UnmarshalState;
import org.exolab.castor.xml.ValidationException;
import org.exolab.castor.xml.XMLClassDescriptor;
import org.exolab.castor.xml.XMLFieldDescriptor;

public class InternalXMLClassDescriptor implements XMLClassDescriptor {
	private XMLClassDescriptor _classDesc = null;
	private XMLFieldDescriptor[] _attributes = null;
	private XMLFieldDescriptor[] _elements = null;
	private FieldDescriptor[] _fields = null;

	protected InternalXMLClassDescriptor(XMLClassDescriptor classDesc) {
		if (classDesc == null) {
			String err = "The argument 'classDesc' must not be null.";
			throw new IllegalArgumentException(err);
		}

		// -- prevent wrapping another InternalXMLClassDescriptor,
		while (classDesc instanceof InternalXMLClassDescriptor) {
			classDesc = ((InternalXMLClassDescriptor) classDesc).getClassDescriptor();
		}
		_classDesc = classDesc;
	}

	/**
	 * Returns the XMLClassDescriptor that this InternalXMLClassDescriptor
	 * wraps.
	 *
	 * @return the XMLClassDescriptor
	 */
	public XMLClassDescriptor getClassDescriptor() {
		return _classDesc;
	} // -- getClassDescriptor

	/**
	 * Returns the set of XMLFieldDescriptors for all members that should be
	 * marshalled as XML attributes. This includes namespace nodes.
	 *
	 * @return an array of XMLFieldDescriptors for all members that should be
	 *         marshalled as XML attributes.
	 */
	public XMLFieldDescriptor[] getAttributeDescriptors() {
		if (_attributes == null) {
			_attributes = _classDesc.getAttributeDescriptors();
		}
		return _attributes;
	} // -- getAttributeDescriptors

	/**
	 * Returns the XMLFieldDescriptor for the member that should be marshalled
	 * as text content.
	 *
	 * @return the XMLFieldDescriptor for the member that should be marshalled
	 *         as text content.
	 */
	public XMLFieldDescriptor getContentDescriptor() {
		return _classDesc.getContentDescriptor();
	} // -- getContentDescriptor

	/**
	 * Returns the XML field descriptor matching the given xml name and
	 * nodeType. If NodeType is null, then either an AttributeDescriptor, or
	 * ElementDescriptor may be returned. Null is returned if no matching
	 * descriptor is available.
	 *
	 * @param name
	 *            the xml name to match against
	 * @param namespace
	 *            the xml namespace to match
	 * @param nodeType
	 *            the NodeType to match against, or null if the node type is not
	 *            known.
	 * @return the matching descriptor, or null if no matching descriptor is
	 *         available.
	 *
	 */
	public XMLFieldDescriptor getFieldDescriptor(final String name, final String namespace, final NodeType nodeType) {
		return _classDesc.getFieldDescriptor(name, namespace, nodeType);
	} // -- getFieldDescriptor

	/**
	 * Returns the set of XMLFieldDescriptors for all members that should be
	 * marshalled as XML elements.
	 *
	 * @return an array of XMLFieldDescriptors for all members that should be
	 *         marshalled as XML elements.
	 */
	public XMLFieldDescriptor[] getElementDescriptors() {
		if (_elements == null) {
			_elements = _classDesc.getElementDescriptors();
		}
		return _elements;
	} // -- getElementDescriptors

	/**
	 * @return the namespace prefix to use when marshalling as XML.
	 */
	public String getNameSpacePrefix() {
		return _classDesc.getNameSpacePrefix();
	} // -- getNameSpacePrefix

	/**
	 * @return the namespace URI used when marshalling and unmarshalling as XML.
	 */
	public String getNameSpaceURI() {
		return _classDesc.getNameSpaceURI();
	} // -- getNameSpaceURI

	/**
	 * Returns a specific validator for the class described by this
	 * ClassDescriptor. A null value may be returned if no specific validator
	 * exists.
	 *
	 * @return the type validator for the class described by this
	 *         ClassDescriptor.
	 */
	public TypeValidator getValidator() {
		return _classDesc.getValidator();
	} // -- getValidator

	/**
	 * Returns the XML Name for the Class being described.
	 *
	 * @return the XML name.
	 */
	public String getXMLName() {
		return _classDesc.getXMLName();
	} // -- getXMLName

	/**
	 * Returns true if the wrapped ClassDescriptor was created by introspection.
	 * 
	 * @return true if the wrapped ClassDescriptor was created by introspection.
	 */
	public boolean introspected() {
		return Introspector.introspected(_classDesc);
	} // -- introspected

	/**
	 * @see org.exolab.castor.xml.XMLClassDescriptor#canAccept(
	 *      java.lang.String, java.lang.String, java.lang.Object)
	 */
	public boolean canAccept(final String name, final String namespace, final Object object) {
		return _classDesc.canAccept(name, namespace, object);
	} // -- canAccept

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.exolab.castor.xml.XMLClassDescriptor#checkDescriptorForCorrectOrderWithinSequence(org.exolab.castor.xml.XMLFieldDescriptor,
	 *      org.exolab.castor.xml.UnmarshalState, java.lang.String)
	 */
	public void checkDescriptorForCorrectOrderWithinSequence(final XMLFieldDescriptor elementDescriptor,
			final UnmarshalState parentState, final String xmlName) throws ValidationException {
		_classDesc.checkDescriptorForCorrectOrderWithinSequence(elementDescriptor, parentState, xmlName);
	}

	// -------------------------------------/
	// - Implementation of ClassDescriptor -/
	// -------------------------------------/

	/**
	 * Returns the Java class represented by this descriptor.
	 *
	 * @return The Java class
	 */
	public Class getJavaClass() {
		return _classDesc.getJavaClass();
	} // -- getJavaClass

	/**
	 * Returns a list of fields represented by this descriptor.
	 *
	 * @return A list of fields
	 */
	public FieldDescriptor[] getFields() {
		if (_fields == null) {
			_fields = _classDesc.getFields();
		}
		return _fields;
	} // -- getFields

	/**
	 * Returns the class descriptor of the class extended by this class.
	 *
	 * @return The extended class descriptor
	 */
	public ClassDescriptor getExtends() {
		return _classDesc.getExtends();
	} // -- getExtends

	/**
	 * Returns the identity field, null if this class has no identity.
	 *
	 * @return The identity field
	 */
	public FieldDescriptor getIdentity() {
		return _classDesc.getIdentity();
	} // -- getIdentity

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.exolab.castor.xml.XMLClassDescriptor#isChoice()
	 */
	public boolean isChoice() {
		return false;
	}
}
