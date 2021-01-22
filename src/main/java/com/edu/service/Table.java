package com.edu.service;

import com.edu.exception.RecordNotFoundException;
import com.edu.exception.UniqueViolationException;
import com.edu.model.Record;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class Table {
  private AtomicLong autoincrement = new AtomicLong(100_000_000); // such number for starting 5-hash unique value
  private final Map<Long, Record> storage = new ConcurrentHashMap<>();
  private final Map<String, Long> index = new ConcurrentHashMap<>();

  public long genId() {
    return autoincrement.incrementAndGet();
  }

  public void save(Record record) {
    long id = record.getId();
    if (storage.containsKey(id)) {
      throw new UniqueViolationException("such record with id "
          + id
          + " already present");
    }
    storage.put(id, record);
    if (record.getKeyword() != null) {
      if (index.containsKey(record.getKeyword())) {
        throw new UniqueViolationException("such record with key "
            + record.getKeyword()
            + " already present");
      }
      index.put(record.getKeyword(), id);
    }
  }

  public Record getByKeyword(String keyword) {
    Long id = index.get(keyword);
    if (id != null) {
      return storage.get(id);
    }
    return null;
  }

  public Record getById(Long id) {
    return Optional
        .ofNullable(storage.get(id))
        .orElseThrow(() -> new RecordNotFoundException("record not found by id " + id));
  }

  public void purge() {
    storage.clear();
    index.clear();
  }
}
