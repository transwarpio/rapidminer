package io.transwarp.midas.impl;

import com.rapidminer.core.io.data.DataSetRow;
import com.rapidminer.core.io.data.ParseException;
import io.transwarp.midas.adaptor.IRow;
import io.transwarp.midas.utils.DateUtil;

import java.util.Date;
import java.util.List;

class MemoryDataRow implements DataSetRow {

  private IRow row;
  private MemoryDataSet memoryDataSet;
  public MemoryDataRow(IRow row, MemoryDataSet memoryDataSet) {
    this.row = row;
    this.memoryDataSet = memoryDataSet;
  }

  @Override
  public Date getDate(int columnIndex) throws ParseException, IndexOutOfBoundsException {
    String value = getString(columnIndex);

    // check for missing value
    if (value == null || value.trim().isEmpty()) {
      return null;
    }

    //TODO: Very ugly code for now, we only have hard-coded date format
    if( memoryDataSet.getSchemas().get(columnIndex).getType().equals("date")) {
      // parse to Date
      try {
        return DateUtil.DATE_FORMAT().parse(value);
      } catch (java.text.ParseException e) {
        throw new ParseException(e.getMessage(), e, columnIndex);
      }

    } else {
      try {
        return DateUtil.TIMESTAMP_FORMAT().parse(value);
      } catch (java.text.ParseException e) {
        throw new ParseException(e.getMessage(), e, columnIndex);
      }
    }
  }

  @Override
  public String getString(int columnIndex) throws ParseException, IndexOutOfBoundsException {
    List<String> values = memoryDataSet.getSchemas().get(columnIndex).getValues();
    if (values != null && values.size() > 0) {
      return values.get((int)Double.parseDouble(row.getValue().get(columnIndex)));
    } else {
      return row.getValue().get(columnIndex);
    }
  }

  @Override
  public double getDouble(int columnIndex) throws ParseException, IndexOutOfBoundsException {
    String v = row.getValue().get(columnIndex);
    return Double.parseDouble(v);
  }

  @Override
  public boolean isMissing(int columnIndex) throws IndexOutOfBoundsException {
    String v = row.getValue().get(columnIndex);
    return v == null || v.equals("\\N");
  }
}
