package sz.api.doc

import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration

//
// Created by kk on 2020/5/6.
//

@Configuration
@ComponentScan(basePackages = ["sz.api.controllers", "sz.api.exceptions", "sz.api.resolve"])
open class ApiDocConfig