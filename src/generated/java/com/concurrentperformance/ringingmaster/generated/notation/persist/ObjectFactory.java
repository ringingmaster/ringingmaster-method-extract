//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2014.06.12 at 07:46:52 AM BST 
//


package com.concurrentperformance.ringingmaster.generated.notation.persist;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the com.concurrentperformance.ringingmaster.generated.notation.persist package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _SerializableNotationList_QNAME = new QName("", "serializableNotationList");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: com.concurrentperformance.ringingmaster.generated.notation.persist
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link SerializableNotationList }
     * 
     */
    public SerializableNotationList createSerializableNotationList() {
        return new SerializableNotationList();
    }

    /**
     * Create an instance of {@link SerializableNotation }
     * 
     */
    public SerializableNotation createSerializableNotation() {
        return new SerializableNotation();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SerializableNotationList }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "serializableNotationList")
    public JAXBElement<SerializableNotationList> createSerializableNotationList(SerializableNotationList value) {
        return new JAXBElement<SerializableNotationList>(_SerializableNotationList_QNAME, SerializableNotationList.class, null, value);
    }

}
