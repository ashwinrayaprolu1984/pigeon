package eu.unicredit.util;

import java.io.IOException;

import org.apache.avro.io.BinaryDecoder;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.specific.SpecificDatumReader;

import transactionlog.Record;

public class AvroDecoder {

	public Record deserialize(byte[] bb) throws IOException{
		
		SpecificDatumReader<Record> sc = new SpecificDatumReader<>(Record.class);

		BinaryDecoder decoder=null;
		decoder= DecoderFactory.get().binaryDecoder(bb, 0,bb.length,decoder);
		Record record= new Record();
		sc.read(record, decoder);
		
		return record;
	}
	
}
