type ITheme = {
	primary: string;
	secondary: string;
	highlight: string;
	overlay: string;
};
export type IMappedTheme = { [key: string]: string | null };
///////////////// End Types /////////
export function applyTheme(theme: IMappedTheme) {
	const root = document.documentElement;
	Object.keys(theme).forEach((cssVar) => {
		root.style.setProperty(cssVar, theme[cssVar]);
	});
}
export const createTheme = (variables: ITheme): IMappedTheme => {
	return {
		"--primary": variables.primary || "",
		"--secondry": variables.secondary || "",
		"--highlight": variables.highlight || "",
		"--overlay": variables.overlay || "",
	};
};
