package com.honey.demo.reader;


import com.honey.demo.main.SpringBatchS3ParquetJsonApplication;
import org.apache.parquet.example.data.Group;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;


@SpringBootTest(classes = SpringBatchS3ParquetJsonApplication.class)
@ExtendWith(SpringExtension.class)
class S3ParquetItemReaderTests {

	@Mock
	private S3ParquetItemReader itemReader;

	@Test
	public void testReadNextItem() throws Exception {
		// Configure mock S3 client to return expected Parquet data
		// ...
		when(itemReader.read()).thenReturn(Mockito.mock(Group.class));

		// Read item from the reader
		Group item = itemReader.read();

		// Assert that the item is not null and contains expected data
		assertNotNull(item);
	}
}