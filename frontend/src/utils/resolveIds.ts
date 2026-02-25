export type ResolvedTags = { tagIds: string[]; tagNames: string[] }

export const resolveCategoryId = (value: string | number | undefined, normalizedCategories: Array<any>): string | undefined => {
  if (value === undefined || value === null || value === '') return undefined
  const s = String(value)
  if (/^\d+$/.test(s)) return s
  const found = normalizedCategories.find((c: any) => String(c.id) === s || String(c.label) === s)
  return found && found.id ? String(found.id) : undefined
}

export const resolveTagArrays = (vals: Array<string | number> | undefined, normalizedTags: Array<any>): ResolvedTags => {
  const tagIds: string[] = []
  const tagNames: string[] = []
  if (!Array.isArray(vals)) return { tagIds, tagNames }
  vals.forEach((v) => {
    const s = String(v)
    if (/^\d+$/.test(s)) {
      tagIds.push(s)
      return
    }
    const found = normalizedTags.find((t: any) => String(t.label) === s || String(t.id) === s)
    if (found && found.id != null && /^\d+$/.test(String(found.id))) tagIds.push(String(found.id))
    else tagNames.push(s)
  })
  return { tagIds, tagNames }
}
