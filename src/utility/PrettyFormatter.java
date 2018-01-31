package utility;

import java.io.IOException;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.PrettyPrinter;

public class PrettyFormatter implements PrettyPrinter {
	protected int arrayDepth = 0;
	protected int objectDepth = 0;
	
	@Override
	public void beforeArrayValues(JsonGenerator jg) throws IOException, JsonGenerationException {
		jg.writeRaw("\n");
		
		for (int i = 0, size = this.arrayDepth + this.objectDepth; i < size; i++) {
			jg.writeRaw("\t");
		}
	}

	@Override
	public void beforeObjectEntries(JsonGenerator jg) throws IOException, JsonGenerationException {
		if (this.arrayDepth == 0) {
			this.objectDepth += 1;
			jg.writeRaw("\n");
			
			for (int i = 0; i < this.objectDepth; i++) {
				jg.writeRaw("\t");
			}
		}
	}

	@Override
	public void writeArrayValueSeparator(JsonGenerator jg) throws IOException, JsonGenerationException {
		jg.writeRaw(",\n");
		
		for (int i = 0, size = this.arrayDepth + this.objectDepth; i < size; i++) {
			jg.writeRaw("\t");
		}
	}

	@Override
	public void writeEndArray(JsonGenerator jg, int nrOfValues) throws IOException, JsonGenerationException {
		jg.writeRaw("\n");
		this.arrayDepth--;
		
		for (int i = 0, size = this.arrayDepth + this.objectDepth; i < size; i++) {
			jg.writeRaw("\t");
		}

		jg.writeRaw("]");
	}

	@Override
	public void writeEndObject(JsonGenerator jg, int nrOfEntries) throws IOException, JsonGenerationException {
		if (this.arrayDepth == 0) {
			jg.writeRaw("\n");
			this.objectDepth--;

			for (int i = 0, size = this.arrayDepth + this.objectDepth; i < size; i++) {
				jg.writeRaw("\t");
			}
		}
		jg.writeRaw("}");
	}

	@Override
	public void writeObjectEntrySeparator(JsonGenerator jg) throws IOException, JsonGenerationException {
		if (this.arrayDepth == 0) {
			jg.writeRaw(",\n");
			
			for (int i = 0, size = this.arrayDepth + this.objectDepth; i < size; i++) {
				jg.writeRaw("\t");
			}
		} else {
			jg.writeRaw(", ");
		}
	}

	@Override
	public void writeObjectFieldValueSeparator(JsonGenerator jg) throws IOException, JsonGenerationException {
		jg.writeRaw(": ");
	}

	@Override
	public void writeRootValueSeparator(JsonGenerator jg) throws IOException, JsonGenerationException {
		jg.writeRaw("@8");
	}

	@Override
	public void writeStartArray(JsonGenerator jg) throws IOException, JsonGenerationException {
		jg.writeRaw("[");
		this.arrayDepth += 1;
	}

	@Override
	public void writeStartObject(JsonGenerator jg) throws IOException, JsonGenerationException {
		jg.writeRaw("{");
	}
}
