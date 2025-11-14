package com.demo.demo_hibernate_search.config;

import org.hibernate.search.backend.lucene.analysis.LuceneAnalysisConfigurationContext;
import org.hibernate.search.backend.lucene.analysis.LuceneAnalysisConfigurer;

public class MyLuceneAnalysisConfigurer implements LuceneAnalysisConfigurer {
    private static final String LOWERCASE = "lowercase";
    @Override
    public void configure(LuceneAnalysisConfigurationContext context) {
        context.analyzer( "myCustomAnalyzer" ).custom()
                .tokenizer( "standard" )
                .charFilter( "htmlStrip" )
                .tokenFilter(LOWERCASE)
                .tokenFilter( "snowballPorter" )
                .param( "language", "French" )
                .tokenFilter( "asciiFolding" );

        context.normalizer(LOWERCASE).custom()
                .tokenFilter(LOWERCASE)
                .tokenFilter( "asciiFolding" );
    }
}