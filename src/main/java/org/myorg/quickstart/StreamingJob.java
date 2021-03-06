/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.myorg.quickstart;

import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.java.tuple.Tuple;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
//import org.apache.flink.streaming.examples.wordcount.util.WordCountData;
import org.apache.flink.util.Collector;
import org.apache.flink.api.java.DataSet;
import java.util.List;

/**
 * Skeleton for a Flink Streaming Job.
 *
 * <p>For a tutorial how to write a Flink streaming application, check the
 * tutorials and examples on the <a href="http://flink.apache.org/docs/stable/">Flink Website</a>.
 *
 * <p>To package your appliation into a JAR file for execution, run
 * 'mvn clean package' on the command line.
 *
 * <p>If you change the name of the main class (with the public static void main(String[] args))
 * method, change the respective entry in the POM.xml file (simply search for 'mainClass').
 */
public class StreamingJob {
    public static final String[] WORDS = new String[] {
            "To be, or not to be,--that is the question:--",
            "Whether 'tis nobler in the mind to suffer",
            "The slings and arrows of outrageous fortune",
            "Or to take arms against a sea of troubles,",
            "And by opposing end them?--To die,--to sleep,--",
            "No more; and by a sleep to say we end",
            "The heartache, and the thousand natural shocks",
            "That flesh is heir to,--'tis a consummation",
            "Devoutly to be wish'd. To die,--to sleep;--",
            "To sleep! perchance to dream:--ay, there's the rub;",
            "For in that sleep of death what dreams may come,",
            "When we have shuffled off this mortal coil,",
            "Must give us pause: there's the respect",
            "That makes calamity of so long life;",
            "For who would bear the whips and scorns of time,",
            "The oppressor's wrong, the proud man's contumely,",
            "The pangs of despis'd love, the law's delay,",
            "The insolence of office, and the spurns",
            "That patient merit of the unworthy takes,",
            "When he himself might his quietus make",
            "With a bare bodkin? who would these fardels bear,",
            "To grunt and sweat under a weary life,",
            "But that the dread of something after death,--",
            "The undiscover'd country, from whose bourn",
            "No traveller returns,--puzzles the will,",
            "And makes us rather bear those ills we have",
            "Than fly to others that we know not of?",
            "Thus conscience does make cowards of us all;",
            "And thus the native hue of resolution",
            "Is sicklied o'er with the pale cast of thought;",
            "And enterprises of great pith and moment,",
            "With this regard, their currents turn awry,",
            "And lose the name of action.--Soft you now!",
            "The fair Ophelia!--Nymph, in thy orisons",
            "Be all my sins remember'd."
    };
    public static final class IntTokenizer implements FlatMapFunction<Integer, Integer> {
        private static final long serialVersionUID = 1L;

        @Override
        public void flatMap(Integer value, Collector<Integer> out) throws Exception {
            out.collect(new Integer(1 + value));
        }
    }
    public static final class LineTokenizer implements FlatMapFunction<String, String> {
        private static final long serialVersionUID = 1L;

        @Override
        public void flatMap(String value, Collector<String> out) throws Exception {
            out.collect(new String("PRE- "+value + " - POST"));
        }
    }
    public static final class Tokenizer1 implements FlatMapFunction<String, Tuple2<String, Integer>> {
        private static final long serialVersionUID = 1L;

        @Override
        public void flatMap(String value, Collector<Tuple2<String, Integer>> out)
                throws Exception {
            // normalize and split the line
            String[] tokens = value.toLowerCase().split("\\W+");
            out.collect(new Tuple2<String, Integer>("IN FLATMAP = " + value.toLowerCase(), 1));

        }
    }
    public static final class Tokenizer implements FlatMapFunction<String, Tuple2<String, Integer>> {
        private static final long serialVersionUID = 1L;

        @Override
        public void flatMap(String value, Collector<Tuple2<String, Integer>> out)
                throws Exception {
            // normalize and split the line
            String[] tokens = value.toLowerCase().split("\\W+");

            // emit the pairs
            for (String token : tokens) {
                if (token.length() > 0) {
                    out.collect(new Tuple2<String, Integer>(token, 1));
                }
            }
        }
    }
    public static void main_string_tuple2(String[] args) throws Exception {
		final ParameterTool params = ParameterTool.fromArgs(args);

		// set up the streaming execution environment
		final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

		// make parameters available in the web interface
		env.getConfig().setGlobalJobParameters(params);

		// get input data
		DataStream<String> text;

        if (params.has("input")) {
            // read the text file from given input path
            text = env.readTextFile(params.get("input"));
            System.out.println("Printing result to stdout. Use --output to specify output path.");

        } else {
            System.out.println("Executing WordCount example with default input data set.");
            System.out.println("Use --input to specify file input.");
            // get default test text data
            text = env.fromElements(WORDS);
        }
//        DataStream<Tuple2<String, Integer>> counts = text.flatMap(new Tokenizer()).keyBy(0).sum(1);
//        if (params.has("output")) {
//            counts.writeAsText(params.get("output"));
//        } else {
//            System.out.println("Printing result to stdout. Use --output to specify output path.");
//            counts.print();
//        }
//        System.out.println("USING JUST FLATMAP");
//        -----------------------------------------
        DataStream<Tuple2<String, Integer>> echo = text.flatMap(new Tokenizer1());
        echo.print();

        if (params.has("output")) {
            echo.writeAsText(params.get("output"));

        } else {
            System.out.println("Printing result to stdout. Use --output to specify output path.");
            echo.print();
        }
//        -----------------------------------------
        env.execute("Flink Streaming Java API Skeleton");
	}
    public static void main_int(String[] args) throws Exception {
        final ParameterTool params = ParameterTool.fromArgs(args);

        // set up the streaming execution environment
        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        // make parameters available in the web interface
        env.getConfig().setGlobalJobParameters(params);
        DataStream<Integer> amounts = env.fromElements(1, 29, 40, 50);
        int threshold = 30;
        DataStream<Integer> a = amounts.flatMap(new IntTokenizer());
        DataStream<Integer> b = a.flatMap(new IntTokenizer());

        if (params.has("output")) {
//            a.writeAsText(params.get("output"));
//            b.writeAsText(params.get("output"));
            a.writeAsText("aaa");
            b.writeAsText("bbb");
        } else {
            System.out.println("Printing result to stdout. Use --output to specify output path.");
            a.print();
            System.out.println("-----------");
            b.print();
        }

        env.execute("Flink Streaming Java API Skeleton");
    }

    public static final class WordTokenizer implements FlatMapFunction<Integer, Tuple2<String, Integer>> {
        private static final long serialVersionUID = 1L;

        @Override
        public void flatMap(Integer value, Collector<Tuple2<String, Integer>> out)
                throws Exception {
            // normalize and split the line
            value = value*value;
            out.collect(new Tuple2<String, Integer>(value+" = ", value));
        }
    }

    public static void main(String[] args) throws Exception {
        final ParameterTool params = ParameterTool.fromArgs(args);

        // set up the streaming execution environment
        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        // make parameters available in the web interface
        env.getConfig().setGlobalJobParameters(params);
        // make parameters available in the web interface
        env.getConfig().setGlobalJobParameters(params);
        DataStream<Integer> amounts = env.fromElements(1, 29, 40, 50);
        int threshold = 30;


        DataStream<String> words = env.fromElements(WORDS);
        DataStream<Integer> a = amounts.flatMap(new IntTokenizer());
        DataStream<Integer> b = a.flatMap(new IntTokenizer());

        DataStream<String> w1 = words.flatMap(new LineTokenizer());
        DataStream<Tuple2<String, Integer>> w2 = b.flatMap(new WordTokenizer());

        if (params.has("output")) {
            w1.writeAsText("aaa");
            w2.writeAsText("bbb");
        } else {
            System.out.println("Printing result to stdout. Use --output to specify output path.");
            w1.print();
            w2.print();
            System.out.println("-----------");
        }
        env.execute("Flink Streaming Java API Skeleton");
    }


}