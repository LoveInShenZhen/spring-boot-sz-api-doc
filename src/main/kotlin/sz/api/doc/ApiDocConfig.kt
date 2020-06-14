package sz.api.doc

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration

//
// Created by kk on 2020/5/6.
//

@Configuration
@ConditionalOnProperty(name = ["sz.api_doc.enabled"], havingValue = "true")
@ComponentScan(basePackages = ["sz.api.controllers", "sz.api.exceptions", "sz.api.resolve"])
open class ApiDocConfig