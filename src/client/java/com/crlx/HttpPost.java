package com.crlx;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.zip.GZIPOutputStream;

public class HttpPost {
  private static byte[] compressData(String data) throws IOException {
    ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
    try (GZIPOutputStream gzipStream = new GZIPOutputStream(byteStream)) {
      gzipStream.write(data.getBytes(StandardCharsets.UTF_8));
    }
    return byteStream.toByteArray();
  }
  public static void main(String data, Boolean retry) throws IOException {
    String url = "https://map.carlox.es/chunk";
    String payload = data;
    byte[] compressedData = compressData(payload);

    try {
      URL obj = new URL(url);
      HttpURLConnection con = (HttpURLConnection) obj.openConnection();

      con.setRequestMethod("POST");
      con.setDoOutput(true);
      con.setDoInput(true);

      con.setRequestProperty("Content-Type", "text/plain");
      con.setRequestProperty("Content-Encoding", "gzip");
      con.setRequestProperty("x-version", "1");
      con.setRequestProperty("x-retry", String.valueOf(retry));

      OutputStream outputStream = con.getOutputStream();
      outputStream.write(compressedData);
      outputStream.flush();
      outputStream.close();
      con.getResponseCode();
    } catch (IOException e) {
      if (retry) {
        System.out.println(e);
      } else {
        main(data, true);
      }
    }
  }
}
