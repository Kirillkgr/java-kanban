package Server.Adapter;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LocalDateTimeTypeAdapter extends TypeAdapter<LocalDateTime> {
	private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
	@Override
	public void write(JsonWriter out, LocalDateTime value) throws IOException {
		out.value(value.format(dateTimeFormatter));
	}
	
	@Override
	public LocalDateTime read(JsonReader in) throws IOException {
		return LocalDateTime.parse(in.nextString(), dateTimeFormatter);
	}
//	@Override
//	public void write(JsonWriter out, LocalDateTime value) throws IOException {
//		out.beginObject();
//		out.name("startTime").value(value.format(dateTimeFormatter));
//		out.endObject();
//	}
//
//	@Override
//	public LocalDateTime read(JsonReader in) throws IOException {
//		in.beginObject();
//		String dateCreated = in.nextString();
//		in.endObject();
//		return LocalDateTime.parse(dateCreated, dateTimeFormatter).;
//	}
}