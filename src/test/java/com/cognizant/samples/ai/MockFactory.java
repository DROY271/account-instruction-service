package com.cognizant.samples.ai;

import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import static org.mockito.Mockito.*;

public class MockFactory {

    private static ResultSetMetaData mockResultSetMetaData(RowDescriptor descriptor) throws SQLException {
        ResultSetMetaData rmd = mock(ResultSetMetaData.class);
        for (int i = 0; i < descriptor.getColumnCount(); i++) {
            String name = descriptor.getName(i + 1);
            int type = descriptor.getType(i + 1);
            doReturn(name).when(rmd).getColumnName(eq(i + 1));
            doReturn(type).when(rmd).getColumnType(eq(i + 1));

        }
        doReturn(descriptor.getColumnCount()).when(rmd).getColumnCount();
        return rmd;
    }

    public static ResultSetMocker resultSet(RowDescriptor desciptor) throws  SQLException {
        return new ResultSetMocker(mockResultSetMetaData(desciptor));
    }



    public static class ResultSetMocker {

        final ResultSet rs = mock(ResultSet.class);

        final ResultSetMetaData md;

        final List<Object[]> columnValues = new LinkedList<>();

        private ResultSetMocker(ResultSetMetaData rmd)  {
            this.md = rmd;
        }

        public ResultSet build() throws SQLException {
            doReturn(md).when(rs).getMetaData();
            for (int i = 0; i < md.getColumnCount(); i++) {
                int index[] = {i};
                List<Object> col = columnValues.stream().map(o -> o[index[0]]).collect(Collectors.toList());
                mockColumn(new ListAnswer(col), md.getColumnName(i + 1), i+1);
            }
            Boolean[] nextVals = new Boolean[columnValues.size() + 1];
            Arrays.fill(nextVals, Boolean.TRUE);
            nextVals[nextVals.length - 1] = false;
            doAnswer(new ListAnswer(Arrays.asList(nextVals))).when(rs).next();
            return rs;
        }

        public ResultSetMocker add(Object...values) throws SQLException {
            if (values.length != md.getColumnCount()) {
                throw new IllegalArgumentException("Values do not match meta-data length");
            }
            columnValues.add(values);
            return this;
        }

        private void mockColumn(Answer answer, String name, int index) throws SQLException {
            String columnName = md.getColumnName(index );
            int type = md.getColumnType(index );
            switch (type) {
                case Types.LONGVARCHAR:
                case Types.VARCHAR:
                case Types.CHAR:
                    doAnswer(answer).when(rs).getString(columnName);
                    doAnswer(answer).when(rs).getString(index);
                    doAnswer(answer).when(rs).getObject(index);
                    break;
                case Types.BOOLEAN:
                case Types.BIT:
                    doAnswer(answer).when(rs).getBoolean(index);
                    doAnswer(answer).when(rs).getBoolean(columnName);
                    doAnswer(answer).when(rs).getObject(index);

                    break;
                case Types.TINYINT:
                    doAnswer(answer).when(rs).getByte(index);
                    doAnswer(answer).when(rs).getByte(columnName);
                    doAnswer(answer).when(rs).getObject(index);
                    doAnswer(answer).when(rs).getObject(columnName);
                    break;
                case Types.SMALLINT:
                    doAnswer(answer).when(rs).getShort(index);
                    doAnswer(answer).when(rs).getShort(columnName);
                    doAnswer(answer).when(rs).getObject(index);
                    doAnswer(answer).when(rs).getObject(columnName);
                    break;
                case Types.INTEGER:
                    doAnswer(answer).when(rs).getInt(index);
                    doAnswer(answer).when(rs).getInt(columnName);
                    doAnswer(answer).when(rs).getObject(index);
                    doAnswer(answer).when(rs).getObject(columnName);
                    break;
                case Types.BIGINT:
                    doAnswer(answer).when(rs).getLong(index);
                    doAnswer(answer).when(rs).getLong(columnName);
                    doAnswer(answer).when(rs).getObject(index);
                    doAnswer(answer).when(rs).getObject(columnName);
                    break;
                case Types.DATE:
                    doAnswer(answer).when(rs).getDate(index);
                    doAnswer(answer).when(rs).getDate(columnName);
                    doAnswer(answer).when(rs).getObject(index);
                    doAnswer(answer).when(rs).getObject(columnName);
                    break;
                case Types.TIME:
                    doAnswer(answer).when(rs).getTime(index);
                    doAnswer(answer).when(rs).getTime(columnName);
                    doAnswer(answer).when(rs).getObject(index);
                    doAnswer(answer).when(rs).getObject(columnName);
                    break;
                case Types.TIMESTAMP:
                    doAnswer(answer).when(rs).getTimestamp(index);
                    doAnswer(answer).when(rs).getTimestamp(columnName);
                    doAnswer(answer).when(rs).getObject(index);
                    doAnswer(answer).when(rs).getObject(columnName);
                    break;
                case Types.REAL:
                    doAnswer(answer).when(rs).getFloat(index);
                    doAnswer(answer).when(rs).getFloat(columnName);
                    doAnswer(answer).when(rs).getObject(index);
                    doAnswer(answer).when(rs).getObject(columnName);
                    break;
                case Types.FLOAT:
                case Types.DOUBLE:
                    doAnswer(answer).when(rs).getDouble(index);
                    doAnswer(answer).when(rs).getDouble(columnName);
                    doAnswer(answer).when(rs).getObject(index);
                    doAnswer(answer).when(rs).getObject(columnName);
                    break;
                default:
                    throw new IllegalArgumentException(String.format("Cannot handle type %d", type));
            }
        }


    }

    private static class ListAnswer implements Answer<Object> {

        final List<Object> values;
        int index;

        ListAnswer(List<Object> obj) {
            this.values = obj;
        }

        @Override
        public Object answer(InvocationOnMock invocation) {
            return values.get(index++);
        }
    }


}
