package com.voidworks.document.repo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication
public class DocumentRepoApplication {

	public static void main(String[] args) {
		SpringApplication.run(DocumentRepoApplication.class, args);

		log.info(
			"\n\nA creation by..." +
			"\n" +
			"\n   _____                             .__    ___________                                          " +
			"\n  /     \\ _____ _______   ____  ____ |  |   \\_   _____/___________    ______ _____  __ __  ______" +
			"\n /  \\ /  \\\\__  \\\\_  __ \\_/ ___\\/ __ \\|  |    |    __)_\\_  __ \\__  \\  /  ___//     \\|  |  \\/  ___/" +
			"\n/    Y    \\/ __ \\|  | \\/\\  \\__\\  ___/|  |__  |        \\|  | \\// __ \\_\\___ \\|  Y Y  \\  |  /\\___ \\ " +
			"\n\\____|__  (____  /__|    \\___  >___  >____/ /_______  /|__|  (____  /____  >__|_|  /____//____  >" +
			"\n        \\/     \\/            \\/    \\/               \\/            \\/     \\/      \\/           \\/"
		);
	}

}
