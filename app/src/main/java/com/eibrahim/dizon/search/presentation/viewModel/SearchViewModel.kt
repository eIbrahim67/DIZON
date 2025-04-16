package com.eibrahim.dizon.search.presentation.viewModel

import androidx.lifecycle.ViewModel
import com.eibrahim.dizon.home.model.PropertyListing

class SearchViewModel : ViewModel() {
    fun getList(): List<PropertyListing> = listOf(
        PropertyListing(
            title = "Villa - 5 Bedrooms - 6 Bathrooms for sale in Chorisia 1 Villas - Al Barari - Dubai",
            imageUrl = "https://www.propertyfinder.ae/property/d5596f824e7b2fe3d8e4820fb91ec05e/416/272/MODE/0df2db/13635501-e8d76o.webp?ctr=ae",
            price = "$1,200,000",
            description = "A luxurious villa in Chorisia 1 with expansive living areas and modern amenities.",
            bedrooms = 5,
            bathrooms = 6,
            propertyType = "Villa"
        ),
        PropertyListing(
            title = "Villa - 3 Bedrooms - 3 Bathrooms for sale in Meadows 9 - Meadows - Dubai",
            imageUrl = "https://www.propertyfinder.ae/property/aabc8ff45bd7d606b615a23f3adf928e/416/272/MODE/35a74d/13614520-ffa59o.webp?ctr=ae",
            price = "$1,200,000",
            description = "An elegant villa in Meadows 9 offering comfort and style.",
            bedrooms = 3,
            bathrooms = 3,
            propertyType = "Villa"
        ),
        PropertyListing(
            title = "Apartment - 2 Bedrooms - 4 Bathrooms for sale in Bluewaters Bay Building 1 - Bluewaters Bay - Bluewaters - Dubai",
            imageUrl = "https://www.propertyfinder.ae/property/12b02a5be8bc4c6fe9d687201869f6f4/416/272/MODE/4d9f5f/13634529-d796co.webp?ctr=ae",
            price = "$800,000",
            description = "A modern apartment in Bluewaters Bay with stunning sea views.",
            bedrooms = 2,
            bathrooms = 4,
            propertyType = "Apartment"
        ),
        PropertyListing(
            title = "Apartment - 2 Bedrooms - 2 Bathrooms for sale in La Vie - Jumeirah Beach Residence - Dubai",
            imageUrl = "https://www.propertyfinder.ae/property/45ebd0c4db2ee14ec4cb803368cad4be/416/272/MODE/0f8f4a/13624487-f1e5fo.webp?ctr=ae",
            price = "$800,000",
            description = "A stylish apartment in La Vie offering vibrant city living.",
            bedrooms = 2,
            bathrooms = 2,
            propertyType = "Apartment"
        ),
        PropertyListing(
            title = "Townhouse - 3 Bedrooms - 4 Bathrooms for sale in Yas Park Gate - Yas Island - Abu Dhabi",
            imageUrl = "https://www.propertyfinder.ae/property/21c3952f973818c00bc9bc5104e65698/416/272/MODE/f2fdd1/13641259-b5db0o.webp?ctr=ae",
            price = "$900,000",
            description = "A contemporary townhouse in Yas Park Gate with a perfect blend of style and comfort.",
            bedrooms = 3,
            bathrooms = 4,
            propertyType = "Townhouse"
        ),
        PropertyListing(
            title = "Apartment - 1 Bedroom - 1 Bathroom for sale in Castleton - Central Park at City Walk - City Walk - Dubai",
            imageUrl = "https://www.propertyfinder.ae/property/d0aff6c54f4a6ef7128bf0331689084f/416/272/MODE/d2077f/13560948-4d9edo.webp?ctr=ae",
            price = "$800,000",
            description = "A cozy apartment in Castleton ideal for modern urban living.",
            bedrooms = 1,
            bathrooms = 1,
            propertyType = "Apartment"
        ),
        PropertyListing(
            title = "Villa - 4 Bedrooms - 4 Bathrooms for sale in Mediterranean Villas - Jumeirah Village Triangle - Dubai",
            imageUrl = "https://www.propertyfinder.ae/property/4e2c7f9388c8f119481e5a16c0dda203/416/272/MODE/0bdb19/13629095-e3203o.webp?ctr=ae",
            price = "$1,200,000",
            description = "A sophisticated villa in Mediterranean Villas with contemporary design.",
            bedrooms = 4,
            bathrooms = 4,
            propertyType = "Villa"
        ),
        PropertyListing(
            title = "Apartment - 2 Bedrooms - 2 Bathrooms for sale in Vida Residence 2 - Vida Residence - The Hills - Dubai",
            imageUrl = "https://www.propertyfinder.ae/property/c169c31d851e2ffcb7c39cd8817315e9/416/272/MODE/5c15f2/13591645-d5ee3o.webp?ctr=ae",
            price = "$800,000",
            description = "A modern apartment in Vida Residence 2 with premium facilities.",
            bedrooms = 2,
            bathrooms = 2,
            propertyType = "Apartment"
        ),
        PropertyListing(
            title = "Apartment - 1 Bedroom - 1 Bathroom for sale in ARAS Residence - Majan - Dubai Land - Dubai",
            imageUrl = "https://www.propertyfinder.ae/property/bdcdf5f3f661d802d57c6c998e851c0c/416/272/MODE/839bf0/13628887-450eao.webp?ctr=ae",
            price = "$800,000",
            description = "A charming apartment in ARAS Residence offering comfortable living.",
            bedrooms = 1,
            bathrooms = 1,
            propertyType = "Apartment"
        ),
        PropertyListing(
            title = "Villa - 5 Bedrooms - 6 Bathrooms for sale in Murooj Al Furjan - Al Furjan - Dubai",
            imageUrl = "https://www.propertyfinder.ae/property/c5bb76c6c2f2eaa5bc99699892a7adc9/416/272/MODE/e340b9/13577111-78094o.webp?ctr=ae",
            price = "$1,200,000",
            description = "A stunning villa in Murooj Al Furjan with luxurious interiors.",
            bedrooms = 5,
            bathrooms = 6,
            propertyType = "Villa"
        ),
        PropertyListing(
            title = "Apartment - 3 Bedrooms - 5 Bathrooms for sale in The Crest - Sobha Hartland - Mohammed Bin Rashid City - Dubai",
            imageUrl = "https://www.propertyfinder.ae/property/d7a2d1a13a55025c9e7055391a8eb6e2/416/272/MODE/80734b/13624202-15279o.webp?ctr=ae",
            price = "$800,000",
            description = "A luxurious apartment in The Crest with high-end finishes.",
            bedrooms = 3,
            bathrooms = 5,
            propertyType = "Apartment"
        ),
        PropertyListing(
            title = "Apartment - 2 Bedrooms - 3 Bathrooms for sale in Vida Residence 3 - Vida Residence - The Hills - Dubai",
            imageUrl = "https://www.propertyfinder.ae/property/c4d66ad2d226eaa3ce323c60d9de0463/416/272/MODE/a1a88a/13578343-dd9ado.webp?ctr=ae",
            price = "$800,000",
            description = "A comfortable apartment in Vida Residence 3 with modern amenities.",
            bedrooms = 2,
            bathrooms = 3,
            propertyType = "Apartment"
        ),
        PropertyListing(
            title = "Villa - 4 Bedrooms - 5 Bathrooms for sale in Entertainment Foyer - European Clusters - Jumeirah Islands - Dubai",
            imageUrl = "https://www.propertyfinder.ae/property/ba97129b437b73b29ed2eccd0a82a991/416/272/MODE/a8b4eb/13641371-1fafeo.webp?ctr=ae",
            price = "$1,200,000",
            description = "An exquisite villa in Entertainment Foyer with contemporary architecture.",
            bedrooms = 4,
            bathrooms = 5,
            propertyType = "Villa"
        ),
        PropertyListing(
            title = "Townhouse - 4 Bedrooms - 4 Bathrooms for sale in Murooj Al Furjan - Al Furjan - Dubai",
            imageUrl = "https://www.propertyfinder.ae/property/f2f0db1f7ffc089385a6a8ee4719600e/416/272/MODE/3441c7/13563027-ec031o.webp?ctr=ae",
            price = "$900,000",
            description = "A modern townhouse in Murooj Al Furjan featuring sleek design and spacious interiors.",
            bedrooms = 4,
            bathrooms = 4,
            propertyType = "Townhouse"
        ),
        PropertyListing(
            title = "Villa - 5 Bedrooms - 6 Bathrooms for sale in Legacy - Jumeirah Park - Dubai",
            imageUrl = "https://www.propertyfinder.ae/property/bbbcfe55a7e1144e5574564c78b2582b/416/272/MODE/cf1402/13593660-939b4o.webp?ctr=ae",
            price = "$1,200,000",
            description = "A luxurious villa in Legacy offering premium amenities and a grand lifestyle.",
            bedrooms = 5,
            bathrooms = 6,
            propertyType = "Villa"
        ),
        PropertyListing(
            title = "Villa - 4 Bedrooms - 6 Bathrooms for sale in Aura - Tilal Al Ghaf - Dubai",
            imageUrl = "https://www.propertyfinder.ae/property/2c75240441db2759a3529fb0aed75eaf/416/272/MODE/5f7e62/13614289-bfdd2o.webp?ctr=ae",
            price = "$1,200,000",
            description = "A beautiful villa in Aura with modern architecture and luxurious details.",
            bedrooms = 4,
            bathrooms = 6,
            propertyType = "Villa"
        ),
        PropertyListing(
            title = "Apartment - Studio - 1 Bathroom for sale in Skyhills Residences - Dubai Science Park - Dubai",
            imageUrl = "https://www.propertyfinder.ae/property/8975c7f38a27c536ae2703f1c61b4c51/416/272/MODE/4c887d/13646142-230f1o.webp?ctr=ae",
            price = "$800,000",
            description = "A compact studio apartment in Skyhills Residences perfect for modern living.",
            bedrooms = 0,
            bathrooms = 1,
            propertyType = "Apartment"
        ),
        PropertyListing(
            title = "Villa - 4 Bedrooms - 5 Bathrooms for sale in Tranquility - Haven By Aldar - Dubai Land - Dubai",
            imageUrl = "https://www.propertyfinder.ae/property/517505ec55c1a07bd4e6c4b8d304a961/416/272/MODE/306b4d/13563058-2bce7o.webp?ctr=ae",
            price = "$1,200,000",
            description = "A serene villa in Tranquility offering a peaceful retreat with modern amenities.",
            bedrooms = 4,
            bathrooms = 5,
            propertyType = "Villa"
        ),
        PropertyListing(
            title = "Apartment - 3 Bedrooms - 4 Bathrooms for sale in Bulgari Resort and Residences - Jumeirah Bay Island - Jumeirah - Dubai",
            imageUrl = "https://www.propertyfinder.ae/property/c023b77d906f60a37feb0f4f2477ba85/416/272/MODE/18445b/13634606-cd910o.webp?ctr=ae",
            price = "$800,000",
            description = "A luxurious apartment in Bulgari Resort and Residences with stunning views.",
            bedrooms = 3,
            bathrooms = 4,
            propertyType = "Apartment"
        ),
        PropertyListing(
            title = "Apartment - 3 Bedrooms - 4 Bathrooms for sale in Bulgari Resort and Residences - Jumeirah Bay Island - Jumeirah - Dubai",
            imageUrl = "https://www.propertyfinder.ae/property/9965fb8a8268c968d9053caeb51db352/416/272/MODE/b2fe7e/13634605-8c9ddo.webp?ctr=ae",
            price = "$800,000",
            description = "A contemporary apartment in Bulgari Resort and Residences offering modern comforts.",
            bedrooms = 3,
            bathrooms = 4,
            propertyType = "Apartment"
        ),
        PropertyListing(
            title = "Apartment - Studio - 1 Bathroom for sale in Rose 2 - Emirates Gardens 1 - Jumeirah Village Circle - Dubai",
            imageUrl = "https://www.propertyfinder.ae/property/a2f92afafc0d8962d00b84589c12f3de/416/272/MODE/415af4/13603717-a87a7o.webp?ctr=ae",
            price = "$800,000",
            description = "A charming studio apartment in Rose 2 with excellent location benefits.",
            bedrooms = 0,
            bathrooms = 1,
            propertyType = "Apartment"
        ),
        PropertyListing(
            title = "Townhouse - 3 Bedrooms - 4 Bathrooms for sale in Mira Oasis 2 - Mira Oasis - Reem - Dubai",
            imageUrl = "https://www.propertyfinder.ae/property/2532a76f2469d18c9472fbb038e508d5/416/272/MODE/c30142/13587629-72d1fo.webp?ctr=ae",
            price = "$900,000",
            description = "A modern townhouse in Mira Oasis 2 offering a comfortable living space.",
            bedrooms = 3,
            bathrooms = 4,
            propertyType = "Townhouse"
        ),
        PropertyListing(
            title = "Villa - 6 Bedrooms - 7+ Bathrooms for sale in Golf Place 2 - Golf Place - Dubai Hills Estate - Dubai",
            imageUrl = "https://www.propertyfinder.ae/property/dd529ec7f5edaaf49fee25731a3e32a4/416/272/MODE/2ad14b/13586970-91655o.webp?ctr=ae",
            price = "$1,200,000",
            description = "An expansive villa in Golf Place 2 offering premium luxury and space.",
            bedrooms = 6,
            bathrooms = 7,
            propertyType = "Villa"
        ),
        PropertyListing(
            title = "Apartment - 3 Bedrooms - 5 Bathrooms for sale in Marina Gate 2 - Marina Gate - Dubai Marina - Dubai",
            imageUrl = "https://www.propertyfinder.ae/property/67af331621d8698238a80d1a9972085f/416/272/MODE/de5033/13605027-752cfo.webp?ctr=ae",
            price = "$800,000",
            description = "A sophisticated apartment in Marina Gate 2 with contemporary design elements.",
            bedrooms = 3,
            bathrooms = 5,
            propertyType = "Apartment"
        ),
        PropertyListing(
            title = "Apartment - 1 Bedroom - 1 Bathroom for sale in Jumeirah Gate Tower 2 - The Address Jumeirah Resort and Spa - Jumeirah Beach Residence - Dubai",
            imageUrl = "https://www.propertyfinder.ae/property/87c9ffde0d3e96c5beb4948c3073e484/416/272/MODE/cca8fd/13606294-c3939o.webp?ctr=ae",
            price = "$800,000",
            description = "A well-appointed apartment in Jumeirah Gate Tower 2 offering modern comfort.",
            bedrooms = 1,
            bathrooms = 1,
            propertyType = "Apartment"
        )
    )

}