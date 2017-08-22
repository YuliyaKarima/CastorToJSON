import org.exolab.castor.xml.*;
import org.castor.xml.BackwardCompatibilityContext;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import java.util.StringTokenizer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.castor.util.Base64Encoder;
import org.castor.util.HexDecoder;
import org.castor.util.Messages;
import org.castor.xml.XMLConfiguration;
import org.exolab.castor.util.SafeStack;
import org.exolab.castor.xml.descriptors.StringClassDescriptor;
import org.exolab.castor.xml.util.AttributeSetImpl;
import org.exolab.castor.xml.util.DocumentHandlerAdapter;
import org.exolab.castor.xml.util.XMLFieldDescriptorImpl;
import org.xml.sax.ContentHandler;
import org.xml.sax.helpers.AttributesImpl;
import org.exolab.castor.util.ReflectionUtil;

public class CastorToJSON extends org.exolab.castor.xml.Marshaller {
	/**
	 * Logger from commons-logging
	 */
	private static final Log LOG = LogFactory.getLog(Marshaller.class);

	/**
	 * The CDATA type..uses for SAX attributes
	 **/
	private static final String CDATA = "CDATA";

	/**
	 * Default prefix for use when creating namespace prefixes.
	 **/
	private static final String DEFAULT_PREFIX = "ns";

	/**
	 * Message name for a non sax capable serializer error
	 **/
	private static final String SERIALIZER_NOT_SAX_CAPABLE = "conf.serializerNotSaxCapable";

	/**
	 * Namespace prefix counter
	 **/
	private int NAMESPACE_COUNTER = 0;

	/**
	 * An instance of StringClassDescriptor
	 **/
	private static final StringClassDescriptor _StringClassDescriptor = new StringClassDescriptor();
	

	// ----------------------------/
	// - Private member variables -/
	// ----------------------------/

	/**
	 * The depth of the sub tree, 0 denotes document level
	 **/
	int depth = 0;
	int fieldCount = 0;

	/**
	 * The ContentHandler we are marshalling to
	 **/
	private ContentHandler _handler = null;

	static final XMLFieldDescriptor[] NO_FIELD_DESCRIPTORS = new XMLFieldDescriptor[0];

	/**
	 * The namespace stack
	 **/
	private Namespaces _namespaces = null;

	/**
	 * current java packages being used during marshalling
	 **/
	private List _packages = null;

	/**
	 * A stack of parent objects...to prevent circular references from being
	 * marshalled.
	 **/
	private Stack _parents = null;

	/**
	 * A list of ProcessingInstructions to output upon marshalling of the
	 * document
	 **/
	private List _processingInstructions = null;

	/**
	 * Name of the root element to use
	 */
	private String _rootElement = null;

	/**
	 * A boolean to indicate keys from a map should be saved when necessary
	 */
	private boolean _saveMapKeys = true;

	/**
	 * The serializer that is being used for marshalling. This may be null if
	 * the user passed in a DocumentHandler.
	 **/
	private MyJSONSerializer _serializer = null;

	/**
	 * A flag to allow suppressing namespaces
	 */
	private boolean _suppressNamespaces = false;

	/**
	 * The set of optional top-level attributes set by the user.
	 **/
	private AttributeSetImpl _topLevelAtts = null;

	/**
	 * The AttributeList which is to be used during marshalling, instead of
	 * creating a bunch of new ones.
	 */
	private AttributesImpl _attributes = null;

	/**
	 * The validation flag
	 */
	private boolean _validate = false;
	private final Set _proxyInterfaces = new HashSet();

	public CastorToJSON(Writer out) throws IOException {
		super(out);
		initialize();
		setWriter(out);

	} // -- Marshaller

	public void setWriter(Writer out) throws IOException {
	
		if (out == null) {
			throw new IllegalArgumentException("Argument 'out' is null.");
		}
		configureSerializer(out);
	}

	private void configureSerializer(Writer out) throws IOException {
		
		_serializer = new MyJSONSerializer();
		

		if (_serializer == null)
			throw new RuntimeException("Unable to obtain serializer");

		_serializer.setOutputCharStream(out);

		// -- Due to a Xerces Serializer bug that doesn't allow declaring
		// -- multiple prefixes to the same namespace, we use the old
		// -- DocumentHandler format and process namespaces ourselves
		_handler = new DocumentHandlerAdapter(_serializer.asDocumentHandler());
		if (_handler == null) {
			String err = Messages.format(SERIALIZER_NOT_SAX_CAPABLE, _serializer.getClass().getName());
			throw new RuntimeException(err);
		}
	}

	private void validate(Object object) throws ValidationException {
		
		if (_validate) {
			// -- we must have a valid element before marshalling
			Validator validator = new Validator();
			ValidationContext context = new ValidationContext();
			context.setInternalContext(getInternalContext());
			// context.setConfiguration(_config);
			// context.setResolver(_cdResolver);
			validator.validate(object, context);
		}
	}

	public void marshal(Object object) throws MarshalException, ValidationException {
		
		if (object == null)
			throw new MarshalException("object must not be null");

		if (LOG.isDebugEnabled()) {
			LOG.debug("Marshalling " + object.getClass().getName());
		}
		validate(object);
		MarshalState mstate = new MarshalState(object, "root");

		marshal(object, null, _handler, mstate);

	} // -- marshal

	public static void marshal(Object object, Writer out) throws MarshalException, ValidationException {
		
		try {
			staticMarshal(object, new CastorToJSON(out));
		} catch (IOException e) {
			throw new MarshalException(e);
		}
	} // -- marshal

	private static void staticMarshal(final Object object, final CastorToJSON marshaller)
			throws MarshalException, ValidationException {
		
		if (object == null) {
			throw new MarshalException("object must not be null");
		}

		if (LOG.isInfoEnabled()) {
			LOG.info("Marshaller called using one of the *static* "
					+ " marshal(Object, *) methods. This will ignore any "
					+ " mapping files as specified. Please consider switching to "
					+ " using Marshaller instances and calling one of the" + " marshal(*) methods.");
		}

		marshaller.marshal(object);
	} // -- staticMarshal

	private void initialize() {
		
		setInternalContext(new BackwardCompatibilityContext());
		_namespaces = new Namespaces();
		_packages = new ArrayList(3);
		_parents = new SafeStack();
		_validate = getInternalContext().marshallingValidation();
		// _naming = XMLNaming.getInstance();
		_processingInstructions = new ArrayList(3);
		_attributes = new AttributesImpl();
		_topLevelAtts = new AttributeSetImpl();
		_saveMapKeys = getInternalContext().getBooleanProperty(XMLConfiguration.SAVE_MAP_KEYS).booleanValue();

		String prop = getInternalContext().getStringProperty(XMLConfiguration.PROXY_INTERFACES);
		if (prop != null) {
			StringTokenizer tokenizer = new StringTokenizer(prop, ", ");
			while (tokenizer.hasMoreTokens()) {
				_proxyInterfaces.add(tokenizer.nextToken());
			}
		}
	} // -- initialize();

	static class MarshalState {

		String xpath = null;
		XMLFieldDescriptor[] nestedAtts = null;

		int nestedAttCount = 0;

		private MarshalState _parent = null;
		private Object _owner = null;
		private String _xmlName = null;

		MarshalState(Object owner, String xmlName) {
			if (owner == null) {
				String err = "The argument 'owner' must not be null";
				throw new IllegalArgumentException(err);
			}
			if (xmlName == null) {
				String err = "The argument 'xmlName' must not be null";
				throw new IllegalArgumentException(err);
			}
			_owner = owner;
			_xmlName = xmlName;
		}

		MarshalState createMarshalState(Object owner, String xmlName) {

			MarshalState ms = new MarshalState(owner, xmlName);
			ms._parent = this;
			return ms;
		}

		String getXPath() {
			if (xpath == null) {
				if (_parent != null) {
					xpath = _parent.getXPath() + "/" + _xmlName;
				} else {
					xpath = _xmlName;
				}
			}
			return xpath;
		}

		Object getOwner() {
			return _owner;
		}

		MarshalState getParent() {
			return _parent;
		}
	}

	private void marshal(Object object, XMLFieldDescriptor descriptor, ContentHandler handler,
			final MarshalState mstate) throws MarshalException, ValidationException {
		boolean mainElem = false;
		

		if (object == null) {
			String err = "Marshaller#marshal: null parameter: 'object'";
			throw new MarshalException(err);
		}
		if (descriptor != null) {
			depth++;
		}

		if (descriptor != null && descriptor.isTransient())
			return;

		boolean containerField = false;
		if (descriptor != null && descriptor.isContainer()) {
			containerField = true;
		}
		// -- add object to stack so we don't potentially get into
		// -- an endlessloop

		if (_parents.search(object) >= 0)
			return;
		else {
			_parents.push(object);

		}

		Class<?> _class = object.getClass();

		boolean byteArray = false;
		boolean atRoot = false;

		if (descriptor == null) {
			descriptor = new XMLFieldDescriptorImpl(_class, "root", null, null);
			atRoot = true;
		}

		// -- calculate Object's name
		String name = descriptor.getXMLName();
		

		if (atRoot && _rootElement != null) {
			name = _rootElement;
			
		}
		if (name == null) {
			mainElem = true;
			name = _class.getName();
			
			// -- remove package information from name
			int idx = name.lastIndexOf('.');
			if (idx >= 0) {
				name = name.substring(idx + 1);
			}
		}

		// -- obtain the class descriptor
		XMLClassDescriptor classDesc = null;
		boolean saveType = false;

		if (_class == descriptor.getFieldType()) {
			
			classDesc = (XMLClassDescriptor) descriptor.getClassDescriptor();
			
		}

		if (classDesc == null) {
			// -- check for primitive or String, we need to use
			// -- the special #isPrimitive method of this class
			// -- so that we can check for the primitive wrapper
			// -- classes
			if (isPrimitive(_class) || byteArray) {
				
				classDesc = _StringClassDescriptor;
				// -- check to see if we need to save the xsi:type
				// -- for this class
				Class<?> fieldType = descriptor.getFieldType();
				

			} else {
				saveType = _class.isArray();
				
				// -- save package information for use when searching
				// -- for MarshalInfo classes
				String className = _class.getName();
				int idx = className.lastIndexOf(".");
				
				// marshall as the base field type
				classDesc = getClassDescriptor(_class);
				
			} // -- end else not primitive

		}

		if (!atRoot) {
			
			_namespaces = _namespaces.createNamespaces();
		}

		// - handle attributes -/

		AttributesImpl atts = new AttributesImpl();

		// -- process attr descriptors
		int nestedAttCount = 0;
		XMLFieldDescriptor[] nestedAtts = null;
		XMLFieldDescriptor[] descriptors = null;
		if (!descriptor.isReference()) {
			
			descriptors = classDesc.getAttributeDescriptors();
		} else {
			// references don't have attributes
			descriptors = NO_FIELD_DESCRIPTORS;
		}
		

		// check if the value is a QName that needs to
		// be resolved ({URI}value -> ns:value)
		// This should be done BEFORE declaring the namespaces as attributes
		// because we can declare new namespace during the QName resolution

		String qName = name;
		
		int firstNonNullIdx = 0;
		try {
			if (!containerField) {
				
				if (mainElem)
					_serializer.startElement2(null, null, qName, atts);
				else
					_serializer.startElement(null, null, qName, atts);
				// handler.startElement(null, null, qName, atts);
			}
		} catch (org.xml.sax.SAXException sx) {
			throw new MarshalException(sx);
		}

		// -- process all child content, including text nodes + daughter
		// elements
		// -- handle text content

		XMLFieldDescriptor cdesc = null;
		if (!descriptor.isReference()) {
			
			cdesc = classDesc.getContentDescriptor();
			
		}

		/* special case for Strings and primitives */
		if (

		isPrimitive(_class)) {
			char[] chars;
			chars = object.toString().toCharArray();
			
			try {
				_serializer.characters(chars, 0, chars.length);
			} catch (org.xml.sax.SAXException sx) {
				throw new MarshalException(sx);
			}

		} else if (isEnum(_class)) {
			char[] chars = object.toString().toCharArray();
			
			try {
				handler.characters(chars, 0, chars.length);
			} catch (org.xml.sax.SAXException sx) {
				throw new MarshalException(sx);
			}

		}

		// -- handle daughter elements
		
		descriptors = classDesc.getElementDescriptors();
		if(descriptors.length!=0)
			fieldCount = descriptors.length;
		
		
		// -- marshal elements of class
		for (int i = firstNonNullIdx; i < descriptors.length; i++) {
			XMLFieldDescriptor elemDescriptor = descriptors[i];
		
			
			Object obj = null;
			// -- obtain value from handler
			try {
				obj = elemDescriptor.getHandler().getValue(object);
				
			} catch (IllegalStateException ise) {
				LOG.warn("Error marshalling " + object, ise);
				continue;
			}
			MarshalState myState = mstate.createMarshalState(object, name);
			myState.nestedAtts = nestedAtts;
			myState.nestedAttCount = nestedAttCount;

			// -- otherwise just marshal object as is
			marshal(obj, elemDescriptor, handler, myState);
		}
		// -- finish element
		
		try {
			if (!containerField) {
				
				if (mainElem)
					_serializer.endElement2(null, null, null);
				else if(depth != fieldCount)
					_serializer.endElement(null, null, null);
				else 
					_serializer.endLastElement(null, null, null);
				// handler.endElement(null, null, null);
				
			}
		} catch (org.xml.sax.SAXException sx) {
			throw new MarshalException(sx);
		}

		_parents.pop();
		mainElem = false;

	} // -- void marshal(DocumentHandler)

	static boolean isPrimitive(final Class<?> type) {
		
		if (type == null) {
			
			return false;
		}
		// -- java primitive
		if (type.isPrimitive()) {
		
			return true;
		}
		// -- we treat strings as primitives
		if (type == String.class) {
			
			return true;
		}
		
		// -- primitive wrapper classes
		if ((type == Boolean.class) || (type == Character.class)) {
			
			return true;
		}
		Class<?> superClass = type.getSuperclass();
		if (superClass == Number.class) {
			
			return true;
		}
		if (superClass != null) {
			
			return superClass.getName().equals("java.lang.Enum");
		}
		return false;
	} // -- isPrimitive

	private XMLClassDescriptor getClassDescriptor(Class<?> _class) throws MarshalException {
		
		XMLClassDescriptor classDesc = null;
		try {
			if (!isPrimitive(_class))
				classDesc = (XMLClassDescriptor) getResolver().resolve(_class);
		} catch (ResolverException rx) {
			Throwable actual = rx.getCause();
			if (actual instanceof MarshalException) {
				throw (MarshalException) actual;
			}
			if (actual != null) {
				throw new MarshalException(actual);
			}
			throw new MarshalException(rx);
		}
		if (classDesc != null)
			classDesc = new InternalXMLClassDescriptor(classDesc);
		
		return classDesc;
	} // -- getClassDescriptor

	static boolean isEnum(final Class<?> type) {
		
		if (type == null) {
			
			return false;
		}
		float javaVersion = Float.valueOf(System.getProperty("java.specification.version")).floatValue();
		
		if (javaVersion >= 1.5) {
			try {
				Boolean isEnum = ReflectionUtil.isEnumViaReflection(type);
				
				return isEnum.booleanValue();
			} catch (Exception e) {
				// nothing to report; implies that there's no such method
			}
		}
		return false;
	} // -- isPrimitive

}
