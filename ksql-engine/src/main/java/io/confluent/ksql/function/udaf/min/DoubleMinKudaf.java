/**
 * Copyright 2017 Confluent Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 **/

package io.confluent.ksql.function.udaf.min;

import io.confluent.ksql.function.AggregateFunctionArguments;
import io.confluent.ksql.function.BaseAggregateFunction;
import io.confluent.ksql.function.KsqlAggregateFunction;
import java.util.Collections;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.streams.kstream.Merger;

public class DoubleMinKudaf extends BaseAggregateFunction<Double, Double> {

  DoubleMinKudaf(String functionName, int argIndexInValue) {
    super(functionName, argIndexInValue, () -> Double.MAX_VALUE, Schema.OPTIONAL_FLOAT64_SCHEMA,
        Collections.singletonList(Schema.OPTIONAL_FLOAT64_SCHEMA),
        "Computes the minimum double value by key."
    );
  }

  @Override
  public Double aggregate(Double currentValue, Double aggregateValue) {
    if (currentValue < aggregateValue) {
      return currentValue;
    }
    return aggregateValue;
  }

  @Override
  public Merger<String, Double> getMerger() {
    return (aggKey, aggOne, aggTwo) -> {
      if (aggOne < aggTwo) {
        return aggOne;
      }
      return aggTwo;
    };
  }

  @Override
  public KsqlAggregateFunction<Double, Double> getInstance(
      final AggregateFunctionArguments aggregateFunctionArguments) {
    return new DoubleMinKudaf(functionName, aggregateFunctionArguments.udafIndex());
  }
}
