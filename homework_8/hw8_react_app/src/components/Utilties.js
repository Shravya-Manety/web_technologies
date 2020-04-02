export function getSectionClassName(sectionName) {
    switch (sectionName) {
        case 'WORLD':
            return 'section-world'
        case 'POLITICS':
            return 'section-politics'
        case 'BUSINESS':
            return 'section-business'
        case 'TECHNOLOGY':
            return 'section-technology'
        case 'SPORTS':
            return 'section-sports'
        default:
            return 'section-other'
    }
}

export function formatDescription(description) {
    let fullStopCount = 0
    let i;
    for (i = 0; i < description.length; i++) {
        if (description.charAt(i) === '.') {
            fullStopCount++
        }
        if (fullStopCount === 4) {
            break
        }
    }
    if (fullStopCount < 4 || (fullStopCount === 4 && description.length === (i + 1))) {
        const basicDesc = description
        return [basicDesc, null]
    }
    if (fullStopCount === 4 && description.length > i) {
        const basicDesc = description.substring(0, i + 1)
        const advancedDesc = description.substring(i + 1)
        return [basicDesc, advancedDesc]
    }
}