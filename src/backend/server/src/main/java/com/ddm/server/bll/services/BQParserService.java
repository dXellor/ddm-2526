package com.ddm.server.bll.services;

import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import com.ddm.server.dll.models.helpers.BQToken;
import com.ddm.server.dll.models.helpers.BQTokenType;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class BQParserService {
    public Query parseQuery(String query){
        query = query.replaceAll("\\s+", " ");
        List<BQToken> tokens = this.parseTokens(query).stream().filter(Objects::nonNull).toList();

        List<Object> processedUnits = new ArrayList<>();

        // Process NOT, add the rest
        for(int i = 0; i < tokens.size(); i++){
            BQToken token = tokens.get(i);
            if(token.getType() == BQTokenType.NOT){
                BQToken nextToken = tokens.get(++i);
                processedUnits.add(Query.of(b -> b.bool(bq -> bq.mustNot(this.queryBasedOnFieldValuePair(nextToken)))));
            }else if( token.getType() == BQTokenType.ST ){
                processedUnits.add(this.queryBasedOnFieldValuePair(token));
            }else{
                processedUnits.add(token);
            }
        }

        // Process AND, add the rest
        List<Object> processedUnits2 = new ArrayList<>();
        for(int i = 0; i < processedUnits.size(); i++){
            Object processedUnit = processedUnits.get(i);
            if(processedUnit instanceof BQToken && ((BQToken) processedUnit).getType() == BQTokenType.AND){
                Query leftQuery = (Query) processedUnits.get(i - 1);
                Query rightQuery = (Query) processedUnits.get(++i);
                processedUnits2.add(Query.of(b -> b.bool(bq -> bq.must(leftQuery).must(rightQuery))));
                i++;
                processedUnits2.remove(leftQuery);
            }else{
                processedUnits2.add(processedUnit);
            }
        }

        // Process OR
        List<Query> processedUnits3 = new ArrayList<>();
        for (Object obj : processedUnits2) {
            if (obj instanceof Query q) {
                processedUnits3.add(q);
            }
        }

        if (processedUnits3.isEmpty()) { return null; }
        if (processedUnits3.size() == 1) { return processedUnits3.get(0); }
        return Query.of(qb -> qb.bool(b -> {
            for (Query q : processedUnits3) {
                b.should(q);
            }
            return b;
        }));
    }

    private List<BQToken> parseTokens(String query){
        String tempQuery = this.replaceSpacesInsideQuotes(query);
        return Arrays.stream(tempQuery.split(" ")).map(part -> {
            if(part.contains(":")){
                return new BQToken(part.split(":")[0], part.split(":")[1], BQTokenType.ST);
            }else{
                switch (part){
                    case "&" -> {
                        return new BQToken("", "", BQTokenType.AND);
                    }
                    case "|" -> {
                        return new BQToken("", "", BQTokenType.OR);
                    }
                    case "!" -> {
                        return new BQToken("", "", BQTokenType.NOT);
                    }
                };
            }
            return null;
        }).toList();
    }

    private Query queryBasedOnFieldValuePair(BQToken token) {
        String field = token.getFieldName();
        String value = token.getFieldValue().replaceAll("%@@%", " ");
        boolean isQuoted = value.startsWith("'") && value.endsWith("'");
        final String finalValue = isQuoted ? value.substring(1, value.length() - 1) : value;
        if (isQuoted) {
            return Query.of(qb -> qb.matchPhrase(mp -> mp.field(field).query(finalValue)));
        } else {
            return Query.of(qb -> qb.match(m -> m
                    .field(field)
                    .fuzziness("AUTO")
                    .query(finalValue)
            ));
        }
    }

    private String replaceSpacesInsideQuotes(String input) {

        Pattern pattern = Pattern.compile("'([^']*)'");
        Matcher matcher = pattern.matcher(input);

        StringBuffer result = new StringBuffer();

        while (matcher.find()) {
            String inside = matcher.group(1);
            String replaced = inside.replace(" ", "%@@%"); // replace spaces inside quotes
            matcher.appendReplacement(result, "'" + Matcher.quoteReplacement(replaced) + "'");
        }

        matcher.appendTail(result);
        return result.toString();
    }
}
