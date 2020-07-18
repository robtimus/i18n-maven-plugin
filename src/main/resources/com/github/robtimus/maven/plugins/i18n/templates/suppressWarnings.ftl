<#if suppressWarnings?size == 1>
@SuppressWarnings("${suppressWarnings?first}")
<#elseif 1 < suppressWarnings?size>
@SuppressWarnings({
    <#list suppressWarnings as w>
        <#assign postfix = (w?index == suppressWarnings?size - 1)?then('', ',')>
    "${w?trim}"${postfix}
    </#list>
})
</#if>
