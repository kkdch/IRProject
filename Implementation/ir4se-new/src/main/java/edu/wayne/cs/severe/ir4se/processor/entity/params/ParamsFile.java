package edu.wayne.cs.severe.ir4se.processor.entity.params;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "parameters")
@XmlAccessorType(XmlAccessType.FIELD)
public class ParamsFile {

	@XmlElement(name = "param")
	List<ConfParam> confParams;

	public List<ConfParam> getConfParams() {
		return confParams;
	}

	public void setConfParams(List<ConfParam> confParms) {
		this.confParams = confParms;
	}

}
