package com.example.apartmentsalesproject.model.data

import com.tickaroo.tikxml.annotation.Element
import com.tickaroo.tikxml.annotation.PropertyElement
import com.tickaroo.tikxml.annotation.Xml

@Xml(name = "response")
data class ApartSales(
    @Element(name = "body")
    val body: Body,
    @Element(name = "header")
    val header: Header
)

@Xml(name = "header")
data class Header(
    @PropertyElement(name = "resultCode")
    val resultCode: Int,
    @PropertyElement(name = "resultMsg")
    val resultMsg: String
)

@Xml(name = "body")
data class Body(
    @Element(name = "items")
    val items: Items,
    @PropertyElement(name = "numOfRows")
    val numOfRows: Int,
    @PropertyElement(name = "pageNo")
    val pageNo: Int,
    @PropertyElement(name = "totalCount")
    val totalCount: Int
)

@Xml(name = "items")
data class Items(
    @Element(name = "item")
    val item: List<Item>
)


@Xml
data class Item(
    @PropertyElement(name = "거래금액")
    var dealAmount: String?,
    @PropertyElement(name = "거래유형")
    var reqGbn: String?,
    @PropertyElement(name = "건축년도")
    var buildYear: String?,
    @PropertyElement(name = "년")
    var dealYear: String?,
    @PropertyElement(name = "등기일자")
    var registrationDate: String?,
    @PropertyElement(name = "법정동")
    var dong: String?,
    @PropertyElement(name = "아파트")
    var apartmentName: String?,
    @PropertyElement(name = "월")
    var dealMonth: String?,
    @PropertyElement(name = "일")
    var dealDay: String?,
    @PropertyElement(name = "전용면적")
    var areaForExclusiveUse: String?,
    @PropertyElement(name = "중재사소재지")
    var rdealerLawdnm: String?,
    @PropertyElement(name = "지번")
    var jibun: String?,
    @PropertyElement(name = "지역코드")
    var regionalCode: String?,
    @PropertyElement(name = "층")
    var sidoCdNm: String?,
    @PropertyElement(name = "해제사유발생일")
    var cancelDealDay: String?,
    @PropertyElement(name = "해제여부")
    var CancelDealType: String?,
)

