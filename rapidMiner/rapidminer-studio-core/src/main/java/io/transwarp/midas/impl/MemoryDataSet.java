/**
 * Copyright (C) 2016 Transwarp Technology(Shanghai ) Co., Ltd.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Affero General Public License as published by the Free Software Foundation, either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License along with this program.
 * If not, see http://www.gnu.org/licenses/.
 */
package io.transwarp.midas.impl;

import com.rapidminer.core.io.data.*;
import com.rapidminer.core.io.data.DataSet;
import com.rapidminer.example.Attributes;
import com.rapidminer.studio.io.data.DefaultColumnMetaData;
import io.transwarp.midas.adaptor.IDataSet;
import io.transwarp.midas.adaptor.IRow;
import io.transwarp.midas.adaptor.ISchema;
import io.transwarp.midas.constant.midas.ColumnRoles;
import io.transwarp.midas.constant.midas.DataTypes;
import io.transwarp.midas.utils.DateUtil;

import java.text.SimpleDateFormat;
import java.util.*;

public class MemoryDataSet implements DataSet{

  private static final Map<String, ColumnMetaData.ColumnType> typeMap= new HashMap<>();

  private static final Map<String, String> attributeMap= new HashMap<>();

  static {
    typeMap.put(DataTypes.DOUBLE(), ColumnMetaData.ColumnType.REAL);
    typeMap.put(DataTypes.FLOAT(), ColumnMetaData.ColumnType.REAL);
    typeMap.put(DataTypes.INT(), ColumnMetaData.ColumnType.INTEGER);
    typeMap.put(DataTypes.SHORT(), ColumnMetaData.ColumnType.INTEGER);
    typeMap.put(DataTypes.STRING(), ColumnMetaData.ColumnType.CATEGORICAL);
    typeMap.put(DataTypes.LONG(), ColumnMetaData.ColumnType.INTEGER);
    typeMap.put(DataTypes.VECTOR(), ColumnMetaData.ColumnType.CATEGORICAL);
    typeMap.put(DataTypes.BOOL(), ColumnMetaData.ColumnType.CATEGORICAL);
    typeMap.put(DataTypes.DATE(), ColumnMetaData.ColumnType.DATE);
    typeMap.put(DataTypes.DECIMAL(), ColumnMetaData.ColumnType.REAL);
    typeMap.put(DataTypes.TIMESTAMP(), ColumnMetaData.ColumnType.DATETIME);
    typeMap.put(DataTypes.STRUCT(), ColumnMetaData.ColumnType.CATEGORICAL);
    typeMap.put(DataTypes.BINARY(), ColumnMetaData.ColumnType.BINARY);

    attributeMap.put(ColumnRoles.ID(), Attributes.ID_NAME);
    attributeMap.put(ColumnRoles.LABEL(), Attributes.LABEL_NAME);
    attributeMap.put(ColumnRoles.FEATURE(), Attributes.ATTRIBUTE_NAME);
    attributeMap.put(ColumnRoles.PREDICTION(), Attributes.PREDICTION_NAME);
  }



  private List<ISchema> schemas;
  private int index = 0;
  private int rowSize = 0;
  private int columnSize = 0;
  Iterator<IRow> row;

  // an empty dataset
  public MemoryDataSet(List<ISchema> schemas) {
    this.schemas = schemas;
    this.rowSize = 0;
    this.columnSize = schemas.size();

    // just give it an empty iterator
    row = new Iterator<IRow>() {
      @Override
      public boolean hasNext() {
        return false;
      }

      @Override
      public IRow next() {
        return null;
      }

      @Override
      public void remove() {

      }
    };
  }

  public MemoryDataSet(IDataSet dataSet) {
    this.schemas = dataSet.getSchema();
    this.rowSize = dataSet.getRows().size();
    this.columnSize = schemas.size();
    row = dataSet.getRows().iterator();
  }

  public List<ISchema> getSchemas() {
    return schemas;
  }

  @Override
  public boolean hasNext() {
    return row.hasNext();
  }

  @Override
  public DataSetRow nextRow() throws DataSetException, NoSuchElementException {
    index += 1;
    return new MemoryDataRow(row.next(), this);
  }

  @Override
  public int getCurrentRowIndex() {
    return index;
  }

  @Override
  public void reset() throws DataSetException {
    index = 0;
  }

  @Override
  public int getNumberOfColumns() {
    return columnSize;
  }

  @Override
  public int getNumberOfRows() {
    return rowSize;
  }

  @Override
  public void close() throws DataSetException {

  }

  public List<ColumnMetaData> getColumnMetaData() {
    List<ColumnMetaData> columnMetaDatas = new LinkedList<>();
    for (ISchema schema : schemas) {
      columnMetaDatas.add(new DefaultColumnMetaData(
              schema.getName(), getColumnType(schema),
              attributeMap.get(schema.getRole()), false));
    }
    return columnMetaDatas;
  }

  /**
   * Get type of the column.
   * (This method is added due to `ui tree rendering` bug)
   *
   * If a schema column is a `label role` and contains any value,
   * it should be regarded as a `categorical columnType`,
   * otherwise, it will be obtained from typemap.
   *
   * @param schema an Ischema;
   * @return type of the column;
   */
  private ColumnMetaData.ColumnType getColumnType(ISchema schema) {
    return schema.getValues().isEmpty() ||
            !schema.getRole().equals(ColumnRoles.LABEL()) ?
            getSchemaType(schema.getType()) : ColumnMetaData.ColumnType.CATEGORICAL;
  }

  private ColumnMetaData.ColumnType getSchemaType(String tpe) {
    if(tpe.startsWith(DataTypes.DECIMAL())){
      return ColumnMetaData.ColumnType.REAL;
    } else {
      return typeMap.get(tpe);
    }
  }
}


