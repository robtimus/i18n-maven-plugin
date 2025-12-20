<#if suppressWarnings?size == 1>
@SuppressWarnings("${suppressWarnings?first}")
<#elseif 1 < suppressWarnings?size>
@SuppressWarnings({
    <#list suppressWarnings as w>
    "${w?trim}"<#if !w?is_last>,</#if>
    </#list>
})
</#if>
