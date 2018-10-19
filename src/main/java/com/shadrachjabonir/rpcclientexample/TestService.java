package com.shadrachjabonir.rpcclientexample;


import com.shadrachjabonir.rpcframeworkexample.model.TestDto;

public interface TestService {
    TestDto makeTest(String name, Integer age, Boolean active);

    TestDto printTest(TestDto testDto);
}
