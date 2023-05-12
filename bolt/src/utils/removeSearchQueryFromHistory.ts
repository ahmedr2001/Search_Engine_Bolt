export default function removeSearchQueryFromHistory(query: string) {
	query = query.trim();
	const searchHistory: { [key: string]: number } = JSON.parse(
		localStorage.getItem("searchHistory") || "{}"
	);
	if (searchHistory[query]) {
		delete searchHistory[query];
		localStorage.setItem("searchHistory", JSON.stringify(searchHistory));
	}
}
