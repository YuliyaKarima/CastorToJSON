import java.io.IOException;
import java.net.ContentHandler;
import java.util.Enumeration;

import org.apache.xerces.dom.DOMMessageFormatter;
import org.apache.xerces.util.XMLChar;
import org.apache.xml.serialize.ElementState;
import org.apache.xml.serialize.XMLSerializer;
import org.exolab.castor.xml.Serializer;
import org.xml.sax.Attributes;
import org.xml.sax.DocumentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

public class MyJSONSerializer extends XMLSerializer {

	public void startElement(String namespaceURI, String localName, String rawName, Attributes attrs)
			throws SAXException {
		
		int i;
		boolean preserveSpace;
		ElementState state;
		String name;
		String value;

		if (DEBUG) {
			System.out.println("==>startElement(" + namespaceURI + "," + localName + "," + rawName + ")");
		}

		try {
			if (_printer == null) {
				String msg = DOMMessageFormatter.formatMessage(DOMMessageFormatter.SERIALIZER_DOMAIN,
						"NoWriterSupplied", null);
				throw new IllegalStateException(msg);
			}

			state = getElementState();
			if (isDocumentState()) {
				// If this is the root element handle it differently.
				// If the first root element in the document, serialize
				// the document's DOCTYPE. Space preserving defaults
				// to that of the output format.
				if (!_started) {
					startDocument((localName == null || localName.length() == 0) ? rawName : localName);
					
					if (state.empty) {
						_printer.printText('{');
						_printer.breakLine();
					}
					state.empty = false;
				}

			} else {
			
				// For any other element, if first in parent, then
				// close parent's opening tag and use the parnet's
				// space preserving.
				// if (state.empty) _printer.printText( '{' );

				// Indent this element on a new line if the first
				// content of the parent element or immediately
				// following an element or a comment
				if (_indenting && !state.preserveSpace && (state.empty || state.afterElement || state.afterComment))
					_printer.breakLine();
			}
			preserveSpace = state.preserveSpace;

			// We remove the namespaces from the attributes list so that they
			// will
			// be in _prefixes
			attrs = extractNamespaces(attrs);
			

			// Do not change the current element state yet.
			// This only happens in endElement().

			_printer.printText('"');
			_printer.printText(rawName);
			_printer.printText('"');
			_printer.printText(':');
			_printer.indent();

			// For each attribute print it's name and value as one part,
			// separated with a space so the element can be broken on
			// multiple lines.
			if (attrs != null) {
				
				for (i = 0; i < attrs.getLength(); ++i) {
					_printer.printSpace();

					name = attrs.getQName(i);
					
					if (name != null && name.length() == 0) {
						String prefix;
						String attrURI;

						name = attrs.getLocalName(i);
						attrURI = attrs.getURI(i);
						if ((attrURI != null && attrURI.length() != 0) && (namespaceURI == null
								|| namespaceURI.length() == 0 || !attrURI.equals(namespaceURI))) {
							prefix = getPrefix(attrURI);
							if (prefix != null && prefix.length() > 0)
								name = prefix + ":" + name;
						}
					}

					value = attrs.getValue(i);
					if (value == null)
						value = "";
					_printer.printText(name);

					_printer.printText("?");
					printEscaped(value);
					_printer.printText('"');

					// If the attribute xml:space exists, determine whether
					// to preserve spaces in this and child nodes based on
					// its value.
				}
			}

			if (_prefixes != null) {
				Enumeration keys;

				keys = _prefixes.keys();
				while (keys.hasMoreElements()) {
					_printer.printSpace();
					value = (String) keys.nextElement();
					name = (String) _prefixes.get(value);
					if (name.length() == 0) {
						_printer.printText("xmlns=\"");
						printEscaped(value);
						_printer.printText('?');
					} else {
						_printer.printText("xmlns:");
						_printer.printText(name);
						_printer.printText("?");
						printEscaped(value);
						_printer.printText('?');
					}
				}
			}

			// Now it's time to enter a new element state
			// with the tag name and space preserving.
			// We still do not change the curent element state.
			state = enterElementState(namespaceURI, localName, rawName, preserveSpace);
			name = (localName == null || localName.length() == 0) ? rawName : namespaceURI + "?" + localName;
			state.doCData = _format.isCDataElement(name);
			state.unescaped = _format.isNonEscapingElement(name);
		} catch (IOException except) {
			throw new SAXException(except);
		}
	}

	public void startElement2(String namespaceURI, String localName, String rawName, Attributes attrs)
			throws SAXException {
		
		int i;
		boolean preserveSpace;
		ElementState state;
		String name;
		String value;

		if (DEBUG) {
			System.out.println("==>startElement(" + namespaceURI + "," + localName + "," + rawName + ")");
		}

		try {
			if (_printer == null) {
				String msg = DOMMessageFormatter.formatMessage(DOMMessageFormatter.SERIALIZER_DOMAIN,
						"NoWriterSupplied", null);
				throw new IllegalStateException(msg);
			}

			state = getElementState();
			if (isDocumentState()) {
				// If this is the root element handle it differently.
				// If the first root element in the document, serialize
				// the document's DOCTYPE. Space preserving defaults
				// to that of the output format.
				if (!_started) {
					startDocument((localName == null || localName.length() == 0) ? rawName : localName);
					
					if (state.empty)
						_printer.printText('{');
					state.empty = false;
				}

			} else {
				
				// For any other element, if first in parent, then
				// close parent's opening tag and use the parnet's
				// space preserving.
				// if (state.empty) _printer.printText( '{' );

				// Indent this element on a new line if the first
				// content of the parent element or immediately
				// following an element or a comment
				if (_indenting && !state.preserveSpace && (state.empty || state.afterElement || state.afterComment))
					_printer.breakLine();
			}
			preserveSpace = state.preserveSpace;

			// We remove the namespaces from the attributes list so that they
			// will
			// be in _prefixes
			attrs = extractNamespaces(attrs);
			

			// For each attribute print it's name and value as one part,
			// separated with a space so the element can be broken on
			// multiple lines.
			if (attrs != null) {
				
				for (i = 0; i < attrs.getLength(); ++i) {
					_printer.printSpace();

					name = attrs.getQName(i);
					
					if (name != null && name.length() == 0) {
						String prefix;
						String attrURI;

						name = attrs.getLocalName(i);
						attrURI = attrs.getURI(i);
						if ((attrURI != null && attrURI.length() != 0) && (namespaceURI == null
								|| namespaceURI.length() == 0 || !attrURI.equals(namespaceURI))) {
							prefix = getPrefix(attrURI);
							if (prefix != null && prefix.length() > 0)
								name = prefix + ":" + name;
						}
					}

					value = attrs.getValue(i);
					if (value == null)
						value = "";
					_printer.printText(name);

					_printer.printText("?");
					printEscaped(value);
					_printer.printText('"');

					// If the attribute xml:space exists, determine whether
					// to preserve spaces in this and child nodes based on
					// its value.
				}
			}

			if (_prefixes != null) {
				Enumeration keys;

				keys = _prefixes.keys();
				while (keys.hasMoreElements()) {
					_printer.printSpace();
					value = (String) keys.nextElement();
					name = (String) _prefixes.get(value);
					if (name.length() == 0) {
						_printer.printText("xmlns=\"");
						printEscaped(value);
						_printer.printText('?');
					} else {
						_printer.printText("xmlns:");
						_printer.printText(name);
						_printer.printText("?");
						printEscaped(value);
						_printer.printText('?');
					}
				}
			}

			// Now it's time to enter a new element state
			// with the tag name and space preserving.
			// We still do not change the curent element state.
			state = enterElementState(namespaceURI, localName, rawName, preserveSpace);
			name = (localName == null || localName.length() == 0) ? rawName : namespaceURI + "?" + localName;
			state.doCData = _format.isCDataElement(name);
			state.unescaped = _format.isNonEscapingElement(name);
		} catch (IOException except) {
			throw new SAXException(except);
		}
	}

	public void endElement(String namespaceURI, String localName, String rawName) throws SAXException {
		
		try {
			endElementIO(namespaceURI, localName, rawName);
		} catch (IOException except) {
			throw new SAXException(except);
		}
	}
	public void endLastElement(String namespaceURI, String localName, String rawName) throws SAXException {
		
		try {
			endLastElementIO(namespaceURI, localName, rawName);
		} catch (IOException except) {
			throw new SAXException(except);
		}
	}

	public void endElement2(String namespaceURI, String localName, String rawName) throws SAXException {
		
		try {
			endElementIO2(namespaceURI, localName, rawName);
		} catch (IOException except) {
			throw new SAXException(except);
		}
	}

	public void endElementIO(String namespaceURI, String localName, String rawName) throws IOException {
		
		ElementState state;
		if (DEBUG) {
			System.out.println("==>endElement: " + rawName);
		}
		// Works much like content() with additions for closing
		// an element. Note the different checks for the closed
		// element's state and the parent element's state.
		_printer.unindent();
		state = getElementState();
		if (state.empty) {
			_printer.printText(",");
		} else {
			// This element is not empty and that last content was
			// another element, so print a line break before that
			// last element and this element's closing tag.
			if (_indenting && !state.preserveSpace && (state.afterElement || state.afterComment))
				_printer.breakLine();
			// _printer.printText(state.rawName);
			_printer.printText(',');
		}
		// Leave the element state and update that of the parent
		// (if we're not root) to not empty and after element.
		state = leaveElementState();
		state.afterElement = true;
		state.afterComment = false;
		state.empty = false;
		if (isDocumentState())
			_printer.flush();
	}
	public void endLastElementIO(String namespaceURI, String localName, String rawName) throws IOException {
		
		ElementState state;
		if (DEBUG) {
			System.out.println("==>endElement: " + rawName);
		}
		// Works much like content() with additions for closing
		// an element. Note the different checks for the closed
		// element's state and the parent element's state.
		_printer.unindent();
		state = getElementState();
		if (state.empty) {
		
		} else {
			// This element is not empty and that last content was
			// another element, so print a line break before that
			// last element and this element's closing tag.
			if (_indenting && !state.preserveSpace && (state.afterElement || state.afterComment))
				_printer.breakLine();
			// _printer.printText(state.rawName);
			
		}
		// Leave the element state and update that of the parent
		// (if we're not root) to not empty and after element.
		state = leaveElementState();
		state.afterElement = true;
		state.afterComment = false;
		state.empty = false;
		if (isDocumentState())
			_printer.flush();
	}

	public void endElementIO2(String namespaceURI, String localName, String rawName) throws IOException {
		
		ElementState state;
		if (DEBUG) {
			System.out.println("==>endElement: " + rawName);
		}
		// Works much like content() with additions for closing
		// an element. Note the different checks for the closed
		// element's state and the parent element's state.
		_printer.unindent();
		state = getElementState();
		if (state.empty) {
			_printer.printText("}");
			_printer.breakLine();
		} else {
			// This element is not empty and that last content was
			// another element, so print a line break before that
			// last element and this element's closing tag.
			if (_indenting && !state.preserveSpace && (state.afterElement || state.afterComment))
				_printer.breakLine();
			// _printer.printText(state.rawName);
			_printer.printText('}');
			_printer.breakLine();
		}
		// Leave the element state and update that of the parent
		// (if we're not root) to not empty and after element.
		state = leaveElementState();
		state.afterElement = true;
		state.afterComment = false;
		state.empty = false;
		if (isDocumentState())
			_printer.flush();
	}

	private Attributes extractNamespaces(Attributes attrs) throws SAXException {
		
		AttributesImpl attrsOnly;
		String rawName;
		int i;
		int length;

		if (attrs == null) {
			return null;
		}
		length = attrs.getLength();
		attrsOnly = new AttributesImpl(attrs);

		for (i = length - 1; i >= 0; --i) {
			rawName = attrsOnly.getQName(i);

			// We have to exclude the namespaces declarations from the
			// attributes
			// Append only when the feature
			// http://xml.org/sax/features/namespace-prefixes"
			// is TRUE
			if (rawName.startsWith("xmlns")) {
				if (rawName.length() == 5) {
					startPrefixMapping("", attrs.getValue(i));
					attrsOnly.removeAttribute(i);
				} else if (rawName.charAt(5) == ':') {
					startPrefixMapping(rawName.substring(6), attrs.getValue(i));
					attrsOnly.removeAttribute(i);
				}
			}
		}
		return attrsOnly;
	}

	public DocumentHandler asDocumentHandler() throws IOException {
		
		prepare();
		return this;
	}

	protected void startDocument(String rootTagName) throws IOException {
		
		if (!_started)
			_started = true;
		// Always serialize these, even if not te first root element.
		// serializePreRoot();
	}

	public void characters(char[] chars, int start, int length) throws SAXException {
		
		try {
			printText(chars, start, length, false, false);
		} catch (IOException except) {
			throw new SAXException(except);
		}

	}

}
