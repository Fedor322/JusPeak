package ru.justspeak.jooq.binding;

import org.jooq.Binding;
import org.jooq.BindingGetResultSetContext;
import org.jooq.BindingGetSQLInputContext;
import org.jooq.BindingGetStatementContext;
import org.jooq.BindingRegisterContext;
import org.jooq.BindingSQLContext;
import org.jooq.BindingSetSQLOutputContext;
import org.jooq.BindingSetStatementContext;
import org.jooq.Converter;
import org.jooq.JSON;
import org.jooq.conf.ParamType;
import org.jooq.impl.DSL;
import ru.justspeak.jooq.converter.JsonStringListConverter;

import java.sql.SQLException;
import java.sql.Types;
import java.util.List;

/** Bridges DDLDatabase's JSON metadata to the PostgreSQL JSONB column. */
public final class ProfilePhotosBinding implements Binding<JSON, List<String>> {

    private static final Converter<JSON, List<String>> CONVERTER = new JsonStringListConverter();

    @Override
    public Converter<JSON, List<String>> converter() {
        return CONVERTER;
    }

    @Override
    public void sql(BindingSQLContext<List<String>> ctx) throws SQLException {
        if (ctx.render().paramType() == ParamType.INLINED) {
            JSON value = CONVERTER.to(ctx.value());
            ctx.render().visit(DSL.inline(value == null ? null : value.data(), String.class));
        } else {
            ctx.render().sql(ctx.variable());
        }
        ctx.render().sql("::jsonb");
    }

    @Override
    public void register(BindingRegisterContext<List<String>> ctx) throws SQLException {
        ctx.statement().registerOutParameter(ctx.index(), Types.VARCHAR);
    }

    @Override
    public void set(BindingSetStatementContext<List<String>> ctx) throws SQLException {
        JSON value = CONVERTER.to(ctx.value());
        if (value == null) {
            ctx.statement().setNull(ctx.index(), Types.VARCHAR);
        } else {
            ctx.statement().setString(ctx.index(), value.data());
        }
    }

    @Override
    public void get(BindingGetResultSetContext<List<String>> ctx) throws SQLException {
        ctx.value(fromText(ctx.resultSet().getString(ctx.index())));
    }

    @Override
    public void get(BindingGetStatementContext<List<String>> ctx) throws SQLException {
        ctx.value(fromText(ctx.statement().getString(ctx.index())));
    }

    @Override
    public void set(BindingSetSQLOutputContext<List<String>> ctx) throws SQLException {
        JSON value = CONVERTER.to(ctx.value());
        ctx.output().writeString(value == null ? null : value.data());
    }

    @Override
    public void get(BindingGetSQLInputContext<List<String>> ctx) throws SQLException {
        ctx.value(fromText(ctx.input().readString()));
    }

    private static List<String> fromText(String value) {
        return CONVERTER.from(value == null ? null : JSON.valueOf(value));
    }
}
