package com.cameronleger.datadumper.exports;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class Helpers {
    public static Element addTextElement(Document doc, String name, String value, Element parent) {
        Element child = doc.createElement(name);
        child.appendChild(doc.createTextNode(value));
        parent.appendChild(child);
        return child;
    }
}
