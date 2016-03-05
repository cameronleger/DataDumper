package com.cameronleger.datadumper.exports;

import javax.xml.transform.Source;

public interface IExport {
    String getFileName();
    Source export();
}
